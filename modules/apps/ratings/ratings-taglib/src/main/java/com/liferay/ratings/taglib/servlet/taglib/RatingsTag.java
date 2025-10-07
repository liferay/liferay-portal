/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ratings.taglib.servlet.taglib;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.PersistedResourcedModelLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.ratings.kernel.RatingsType;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionUtil;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;
import com.liferay.ratings.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.List;
import java.util.Objects;

/**
 * @author Ambrín Chaudhary
 */
public class RatingsTag extends IncludeTag {

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getContentTitle() {
		return _contentTitle;
	}

	public int getNumberOfStars() {
		return _numberOfStars;
	}

	public RatingsEntry getRatingsEntry() {
		return _ratingsEntry;
	}

	public RatingsStats getRatingsStats() {
		return _ratingsStats;
	}

	public String getType() {
		return _type;
	}

	public String getUrl() {
		return _url;
	}

	public boolean isInTrash() {
		return _inTrash;
	}

	public void setClassName(String className) {
		if (className.equals(DLFileEntry.class.getName()) ||
			className.equals(FileEntry.class.getName()) ||
			className.equals(LiferayFileEntry.class.getName())) {

			className = DLFileEntryConstants.getClassName();
		}

		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setContentTitle(String contentTitle) {
		_contentTitle = contentTitle;
	}

	public void setInTrash(boolean inTrash) {
		_inTrash = inTrash;
	}

	public void setNumberOfStars(int numberOfStars) {
		_numberOfStars = numberOfStars;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setRatingsEntry(RatingsEntry ratingsEntry) {
		_ratingsEntry = ratingsEntry;

		_setRatingsEntry = true;
	}

	public void setRatingsStats(RatingsStats ratingsStats) {
		_ratingsStats = ratingsStats;

		_setRatingsStats = true;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setUrl(String url) {
		_url = url;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_className = null;
		_classPK = 0;
		_contentTitle = null;
		_externalReferenceCode = null;
		_inTrash = null;
		_numberOfStars = _NUMBER_OF_STARS;
		_ratingsEntry = null;
		_ratingsStats = null;
		_setRatingsEntry = false;
		_setRatingsStats = false;
		_type = null;
		_url = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		try {
			httpServletRequest.setAttribute(
				"liferay-ratings:ratings:className", _className);
			httpServletRequest.setAttribute(
				"liferay-ratings:ratings:classPK", String.valueOf(_classPK));
			httpServletRequest.setAttribute(
				"liferay-ratings:ratings:externalReferenceCode",
				_getExternalReferenceCode());

			boolean inTrash = _isInTrash();

			RatingsStats ratingsStats = _getRatingsStats();

			int positiveVotes = (int)Math.round(_getTotalScore(ratingsStats));

			String randomNamespace = PortalUtil.generateRandomKey(
				getRequest(), "taglib_ratings_ratings_");

			randomNamespace += StringPool.UNDERLINE;

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			RatingsEntry ratingsEntry = _getRatingsEntry(
				ratingsStats, themeDisplay);

			double userScore = _getUserScore(ratingsEntry);

			boolean thumbUp = _isThumbUp(userScore);

			int totalEntries = _getTotalEntries(ratingsStats);
			String type = _getType(httpServletRequest);
			String url = _getURL(themeDisplay);

			httpServletRequest.setAttribute(
				"liferay-ratings:ratings:data",
				HashMapBuilder.<String, Object>put(
					"className", _className
				).put(
					"classPK", _classPK
				).put(
					"contentTitle", _contentTitle
				).put(
					"enabled", _isEnabled(themeDisplay, inTrash)
				).put(
					"externalReferenceCode", _externalReferenceCode
				).put(
					"initialAverageScore", _getInitialAverageScore(ratingsStats)
				).put(
					"initialLiked", thumbUp
				).put(
					"initialNegativeVotes", totalEntries - positiveVotes
				).put(
					"initialPositiveVotes", positiveVotes
				).put(
					"initialTotalEntries", totalEntries
				).put(
					"inTrash", inTrash
				).put(
					"numberOfStars", getNumberOfStars()
				).put(
					"positiveVotes", positiveVotes
				).put(
					"randomNamespace", randomNamespace
				).put(
					"signedIn", themeDisplay.isSignedIn()
				).put(
					"thumbDown", _isThumbDown(userScore)
				).put(
					"thumbUp", thumbUp
				).put(
					"type", type
				).put(
					"url", url
				).put(
					"userScore", userScore
				).build());

			httpServletRequest.setAttribute(
				"liferay-ratings:ratings:inTrash", inTrash);
			httpServletRequest.setAttribute(
				"liferay-ratings:ratings:ratingsEntry", ratingsEntry);
			httpServletRequest.setAttribute(
				"liferay-ratings:ratings:ratingsStats", ratingsStats);
			httpServletRequest.setAttribute(
				"liferay-ratings:ratings:type", type);
			httpServletRequest.setAttribute("liferay-ratings:ratings:url", url);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private String _getExternalReferenceCode() throws Exception {
		if (Validator.isNotNull(_externalReferenceCode)) {
			return _externalReferenceCode;
		}

		String className = _className;

		if (Objects.equals(className, MBDiscussion.class.getName())) {
			className = MBMessage.class.getName();
		}

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(className);

		PersistedModel persistedModel = null;

		if (persistedModelLocalService instanceof
				PersistedResourcedModelLocalService) {

			PersistedResourcedModelLocalService
				persistedResourcedModelLocalService =
					(PersistedResourcedModelLocalService)
						persistedModelLocalService;

			List<? extends PersistedModel> persistedModels =
				persistedResourcedModelLocalService.getPersistedModel(_classPK);

			if (!persistedModels.isEmpty()) {
				persistedModel = persistedModels.get(0);
			}
		}

		if (persistedModel == null) {
			persistedModel = persistedModelLocalService.fetchPersistedModel(
				_classPK);
		}

		if (persistedModel instanceof ExternalReferenceCodeModel) {
			ExternalReferenceCodeModel externalReferenceCodeModel =
				(ExternalReferenceCodeModel)persistedModel;

			_externalReferenceCode =
				externalReferenceCodeModel.getExternalReferenceCode();
		}

		return _externalReferenceCode;
	}

	private double _getInitialAverageScore(RatingsStats ratingsStats) {
		if (ratingsStats != null) {
			return ratingsStats.getAverageScore();
		}

		return 0;
	}

	private RatingsEntry _getRatingsEntry(
		RatingsStats ratingsStats, ThemeDisplay themeDisplay) {

		if (!_setRatingsEntry && (ratingsStats != null)) {
			return RatingsEntryLocalServiceUtil.fetchEntry(
				themeDisplay.getUserId(), _className, _classPK);
		}

		return _ratingsEntry;
	}

	private RatingsStats _getRatingsStats() {
		if (!_setRatingsStats) {
			return RatingsStatsLocalServiceUtil.fetchStats(
				_className, _classPK);
		}

		return _ratingsStats;
	}

	private int _getTotalEntries(RatingsStats ratingsStats) {
		int totalEntries = 0;

		if (ratingsStats != null) {
			totalEntries = ratingsStats.getTotalEntries();
		}

		return totalEntries;
	}

	private double _getTotalScore(RatingsStats ratingsStats) {
		if (ratingsStats != null) {
			return ratingsStats.getTotalScore();
		}

		return 0.0;
	}

	private String _getType(HttpServletRequest httpServletRequest) {
		if (Validator.isNotNull(_type)) {
			return _type;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getSiteGroup();

		if (group.isStagingGroup()) {
			group = group.getLiveGroup();
		}

		RatingsType ratingsType = null;

		if (group != null) {
			try {
				ratingsType = PortletRatingsDefinitionUtil.getRatingsType(
					themeDisplay.getCompanyId(), group.getGroupId(),
					_className);
			}
			catch (PortalException portalException) {
				_log.error(
					"Unable to get ratings type for group " +
						group.getGroupId(),
					portalException);
			}
		}

		if (ratingsType == null) {
			ratingsType = RatingsType.STARS;
		}

		return ratingsType.getValue();
	}

	private String _getURL(ThemeDisplay themeDisplay) {
		if (Validator.isNull(_url)) {
			return themeDisplay.getPathMain() + "/portal/rate_entry";
		}

		return _url;
	}

	private double _getUserScore(RatingsEntry ratingsEntry) {
		double userScore = -1.0;

		if (ratingsEntry != null) {
			userScore = ratingsEntry.getScore();
		}

		return userScore;
	}

	private boolean _isEnabled(ThemeDisplay themeDisplay, boolean inTrash) {
		if (!inTrash) {
			Group group = themeDisplay.getSiteGroup();

			if (!group.isStagingGroup() && !group.isStagedRemotely()) {
				return true;
			}
		}

		return false;
	}

	private boolean _isInTrash() throws PortalException {
		if (_inTrash == null) {
			TrashHandler trashHandler =
				TrashHandlerRegistryUtil.getTrashHandler(_className);

			if (trashHandler == null) {
				return false;
			}

			return trashHandler.isInTrash(_classPK);
		}

		return _inTrash;
	}

	private boolean _isThumbDown(double userScore) {
		if ((userScore != -1.0) && (userScore < 0.5)) {
			return true;
		}

		return false;
	}

	private boolean _isThumbUp(double userScore) {
		if ((userScore != -1.0) && (userScore >= 0.5)) {
			return true;
		}

		return false;
	}

	private static final int _NUMBER_OF_STARS = GetterUtil.getInteger(
		PropsUtil.get(PropsKeys.RATINGS_DEFAULT_NUMBER_OF_STARS));

	private static final String _PAGE = "/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(RatingsTag.class);

	private String _className;
	private long _classPK;
	private String _contentTitle;
	private String _externalReferenceCode;
	private Boolean _inTrash;
	private int _numberOfStars = _NUMBER_OF_STARS;
	private RatingsEntry _ratingsEntry;
	private RatingsStats _ratingsStats;
	private boolean _setRatingsEntry;
	private boolean _setRatingsStats;
	private String _type;
	private String _url;

}
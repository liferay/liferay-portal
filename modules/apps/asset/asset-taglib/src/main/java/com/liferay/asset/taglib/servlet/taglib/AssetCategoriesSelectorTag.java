/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.asset.taglib.internal.item.selector.ItemSelectorUtil;
import com.liferay.asset.taglib.internal.servlet.ServletContextUtil;
import com.liferay.asset.taglib.internal.util.AssetCategoryUtil;
import com.liferay.asset.taglib.internal.util.AssetVocabularyUtil;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.learn.LearnMessage;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.asset.util.comparator.AssetVocabularyGroupLocalizedTitleComparator;
import com.liferay.taglib.aui.AUIUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Chema Balsas
 * @author Carlos Lancha
 */
public class AssetCategoriesSelectorTag extends IncludeTag {

	public String getCategoryIds() {
		return _categoryIds;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public long getClassTypePK() {
		return _classTypePK;
	}

	public String getHiddenInput() {
		return _hiddenInput;
	}

	public String getId() {
		return _id;
	}

	public int[] getVisibilityTypes() {
		return _visibilityTypes;
	}

	public boolean isIgnoreRequestValue() {
		return _ignoreRequestValue;
	}

	public boolean isShowLabel() {
		return _showLabel;
	}

	public boolean isShowOnlyRequiredVocabularies() {
		return _showOnlyRequiredVocabularies;
	}

	public boolean isShowRequiredLabel() {
		return _showRequiredLabel;
	}

	public boolean isSingleSelect() {
		return _singleSelect;
	}

	public boolean isUseDataCategoriesAttribute() {
		return _useDataCategoriesAttribute;
	}

	public void setCategoryIds(String categoryIds) {
		_categoryIds = categoryIds;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setClassTypePK(long classTypePK) {
		_classTypePK = classTypePK;
	}

	public void setGroupIds(long[] groupIds) {
		_groupIds = groupIds;
	}

	public void setHiddenInput(String hiddenInput) {
		_hiddenInput = hiddenInput;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIgnoreRequestValue(boolean ignoreRequestValue) {
		_ignoreRequestValue = ignoreRequestValue;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setShowLabel(boolean showLabel) {
		_showLabel = showLabel;
	}

	public void setShowOnlyRequiredVocabularies(
		boolean showOnlyRequiredVocabularies) {

		_showOnlyRequiredVocabularies = showOnlyRequiredVocabularies;
	}

	public void setShowRequiredLabel(boolean showRequiredLabel) {
		_showRequiredLabel = showRequiredLabel;
	}

	public void setSingleSelect(boolean singleSelect) {
		_singleSelect = singleSelect;
	}

	public void setUseDataCategoriesAttribute(
		boolean useDataCategoriesAttribute) {

		_useDataCategoriesAttribute = useDataCategoriesAttribute;
	}

	public void setVisibilityTypes(int[] visibilityTypes) {
		_visibilityTypes = visibilityTypes;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_categoryIds = null;
		_className = null;
		_classPK = 0;
		_classTypePK = AssetCategoryConstants.ALL_CLASS_TYPE_PK;
		_groupIds = null;
		_hiddenInput = "assetCategoryIds";
		_id = null;
		_ignoreRequestValue = false;
		_namespace = null;
		_showLabel = true;
		_showOnlyRequiredVocabularies = false;
		_showRequiredLabel = true;
		_singleSelect = false;
		_useDataCategoriesAttribute = false;
		_visibilityTypes = _VISIBILITY_TYPES;
	}

	protected List<String[]> getCategoryIdsTitles() {
		List<String[]> categoryIdsTitles = new ArrayList<>();

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String categoryIds = StringPool.BLANK;

		if (Validator.isNotNull(_categoryIds)) {
			categoryIds = _categoryIds;
		}

		try {
			for (AssetVocabulary vocabulary : _getVocabularies()) {
				String categoryNames = StringPool.BLANK;

				if (Validator.isNotNull(_className) && (_classPK > 0)) {
					List<AssetCategory> assetCategories =
						AssetCategoryServiceUtil.getCategories(
							_className, _classPK);

					categoryIds = ListUtil.toString(
						assetCategories, AssetCategory.CATEGORY_ID_ACCESSOR);
					categoryNames = ListUtil.toString(
						assetCategories, AssetCategory.NAME_ACCESSOR);
				}

				if (!_ignoreRequestValue) {
					if (Validator.isNotNull(_className)) {
						String[] categoryIdsParam =
							httpServletRequest.getParameterValues(
								_hiddenInput + StringPool.UNDERLINE +
									vocabulary.getVocabularyId());

						if (categoryIdsParam != null) {
							categoryIds = StringUtil.merge(
								categoryIdsParam, StringPool.COMMA);
						}
					}
					else {
						String categoryIdsParam =
							httpServletRequest.getParameter(_hiddenInput);

						if (categoryIdsParam != null) {
							categoryIds = categoryIdsParam;
						}
					}
				}

				String[] categoryIdsTitle =
					AssetCategoryUtil.getCategoryIdsTitles(
						categoryIds, categoryNames,
						vocabulary.getVocabularyId(), themeDisplay);

				categoryIdsTitles.add(categoryIdsTitle);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return categoryIdsTitles;
	}

	protected long[] getGroupIds() {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (ArrayUtil.isEmpty(_groupIds)) {
				return SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						_getGroupId(themeDisplay.getScopeGroup()));
			}

			return SiteConnectedGroupGroupProviderUtil.
				getCurrentAndAncestorSiteAndDepotGroupIds(_groupIds);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return new long[0];
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	protected PortletURL getPortletURL() {
		ItemSelector itemSelector = ItemSelectorUtil.getItemSelector();

		HttpServletRequest httpServletRequest = getRequest();

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(AssetCategory.class.getName());
		itemSelectorCriterion.setMultiSelection(true);

		return PortletURLBuilder.create(
			itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, themeDisplay.getScopeGroup(),
				themeDisplay.getScopeGroupId(), "selectCategory",
				itemSelectorCriterion)
		).setParameter(
			"showAddCategoryButton", true
		).buildPortletURL();
	}

	protected List<Map<String, Object>> getVocabularies() throws Exception {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<String[]> categoryIdsTitles = getCategoryIdsTitles();
		IntegerWrapper index = new IntegerWrapper(-1);
		List<AssetVocabulary> assetVocabularies = _getVocabularies();

		return TransformUtil.transform(
			assetVocabularies,
			assetVocabulary -> {
				index.increment();

				if (!ArrayUtil.contains(
						getVisibilityTypes(),
						assetVocabulary.getVisibilityType())) {

					return null;
				}

				String selectedCategoryIds =
					categoryIdsTitles.get(index.getValue())[0];

				return HashMapBuilder.<String, Object>put(
					"id", assetVocabulary.getVocabularyId()
				).put(
					"required",
					assetVocabulary.isRequired(
						PortalUtil.getClassNameId(_className), _classTypePK,
						themeDisplay.getScopeGroupId()) &&
					_showRequiredLabel
				).put(
					"selectedCategories", selectedCategoryIds
				).put(
					"selectedItems",
					() -> {
						if (Validator.isNull(selectedCategoryIds)) {
							return null;
						}

						List<Map<String, Object>> selectedItems =
							new ArrayList<>();

						String[] categoryIds = selectedCategoryIds.split(",");

						String selectedCategoryIdTitles =
							categoryIdsTitles.get(index.getValue())[1];

						String[] categoryTitles =
							selectedCategoryIdTitles.split(
								AssetCategoryUtil.CATEGORY_SEPARATOR);

						for (int j = 0; j < categoryIds.length; j++) {
							selectedItems.add(
								HashMapBuilder.<String, Object>put(
									"label", categoryTitles[j]
								).put(
									"value", categoryIds[j]
								).build());
						}

						return selectedItems;
					}
				).put(
					"singleSelect",
					_singleSelect || !assetVocabulary.isMultiValued()
				).put(
					"title",
					assetVocabulary.getUnambiguousTitle(
						assetVocabularies, themeDisplay.getScopeGroupId(),
						themeDisplay.getLocale())
				).put(
					"visibilityType", assetVocabulary.getVisibilityType()
				).build();
			});
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		try {
			httpServletRequest.setAttribute(
				"liferay-asset:asset-categories-selector:data",
				HashMapBuilder.<String, Object>put(
					"eventName", "selectCategory"
				).put(
					"groupIds", ListUtil.fromArray(getGroupIds())
				).put(
					"id", _getNamespace() + _getId() + "assetCategoriesSelector"
				).put(
					"inputName", _getInputName()
				).put(
					"learnHowLink",
					() -> {
						ThemeDisplay themeDisplay =
							(ThemeDisplay)httpServletRequest.getAttribute(
								WebKeys.THEME_DISPLAY);

						LearnMessage learnMessage =
							LearnMessageUtil.getLearnMessage(
								"general", themeDisplay.getLanguageId(),
								"asset-taglib");

						return JSONUtil.put(
							"message", learnMessage.getMessage()
						).put(
							"url", learnMessage.getURL()
						);
					}
				).put(
					"portletURL", String.valueOf(getPortletURL())
				).put(
					"showLabel", isShowLabel()
				).put(
					"useDataCategoriesAttribute", isUseDataCategoriesAttribute()
				).put(
					"vocabularies", getVocabularies()
				).build());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private long _getGroupId(Group group) throws PortalException {
		if (group.isLayoutPrototype()) {
			LayoutPrototype layoutPrototype =
				LayoutPrototypeLocalServiceUtil.getLayoutPrototype(
					group.getClassPK());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchFirstLayoutPageTemplateEntry(
						layoutPrototype.getLayoutPrototypeId());

			if ((layoutPageTemplateEntry != null) &&
				(layoutPageTemplateEntry.getGroupId() > 0)) {

				group = GroupLocalServiceUtil.getGroup(
					layoutPageTemplateEntry.getGroupId());
			}
		}

		return group.getGroupId();
	}

	private String _getId() {
		if (Validator.isNotNull(_id)) {
			return _id;
		}

		String randomKey = PortalUtil.generateRandomKey(
			getRequest(), "taglib_ui_asset_categories_selector_page");

		return randomKey + StringPool.UNDERLINE;
	}

	private String _getInputName() {
		return _getNamespace() + _hiddenInput + StringPool.UNDERLINE;
	}

	private String _getNamespace() {
		if (_namespace != null) {
			return _namespace;
		}

		HttpServletRequest httpServletRequest = getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		if ((portletRequest == null) || (portletResponse == null)) {
			_namespace = AUIUtil.getNamespace(httpServletRequest);

			return _namespace;
		}

		_namespace = AUIUtil.getNamespace(portletRequest, portletResponse);

		return _namespace;
	}

	private List<AssetVocabulary> _getVocabularies() {
		List<AssetVocabulary> assetVocabularies = new ArrayList<>();

		assetVocabularies.addAll(
			AssetVocabularyServiceUtil.getGroupVocabularies(
				getGroupIds(), _visibilityTypes));

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		assetVocabularies.sort(
			new AssetVocabularyGroupLocalizedTitleComparator(
				themeDisplay.getScopeGroupId(), themeDisplay.getLocale(),
				true));

		if (Validator.isNotNull(_className)) {
			assetVocabularies = AssetVocabularyUtil.filterVocabularies(
				assetVocabularies, _className, _classTypePK);
		}

		return ListUtil.filter(
			assetVocabularies,
			assetVocabulary -> {
				if (_showOnlyRequiredVocabularies &&
					!assetVocabulary.isRequired(
						PortalUtil.getClassNameId(_className), _classTypePK,
						themeDisplay.getScopeGroupId())) {

					return false;
				}

				return true;
			});
	}

	private static final String _PAGE = "/asset_categories_selector/page.jsp";

	private static final int[] _VISIBILITY_TYPES = {
		AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC
	};

	private static final Log _log = LogFactoryUtil.getLog(
		AssetCategoriesSelectorTag.class);

	private String _categoryIds;
	private String _className;
	private long _classPK;
	private long _classTypePK = AssetCategoryConstants.ALL_CLASS_TYPE_PK;
	private long[] _groupIds;
	private String _hiddenInput = "assetCategoryIds";
	private String _id;
	private boolean _ignoreRequestValue;
	private String _namespace;
	private boolean _showLabel = true;
	private boolean _showOnlyRequiredVocabularies;
	private boolean _showRequiredLabel = true;
	private boolean _singleSelect;
	private boolean _useDataCategoriesAttribute;
	private int[] _visibilityTypes = _VISIBILITY_TYPES;

}
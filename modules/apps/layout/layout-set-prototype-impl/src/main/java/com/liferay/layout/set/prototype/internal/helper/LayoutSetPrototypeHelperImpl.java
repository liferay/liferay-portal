/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.internal.helper;

import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototypeTable;
import com.liferay.portal.kernel.model.LayoutSetTable;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.sites.kernel.util.Sites;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = LayoutSetPrototypeHelper.class)
public class LayoutSetPrototypeHelperImpl implements LayoutSetPrototypeHelper {

	@Override
	public List<Layout> getDuplicatedFriendlyURLLayouts(Layout layout)
		throws PortalException {

		Group group = layout.getGroup();

		if (group.isLayoutSetPrototype()) {
			return _getDuplicatedFriendlyURLSiteLayouts(layout);
		}

		LayoutSet layoutSet = layout.getLayoutSet();

		if (!layoutSet.isLayoutSetPrototypeLinkActive()) {
			return Collections.emptyList();
		}

		Layout conflictLayout = _getDuplicatedFriendlyURLPrototypeLayout(
			layout);

		if (conflictLayout != null) {
			return Collections.singletonList(conflictLayout);
		}

		return Collections.emptyList();
	}

	@Override
	public List<Long> getDuplicatedFriendlyURLPlids(LayoutSet layoutSet) {
		LayoutTable tempLayoutTable = LayoutTable.INSTANCE.as(
			"tempLayoutTable");

		return _layoutLocalService.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				LayoutTable.INSTANCE.plid
			).from(
				LayoutTable.INSTANCE
			).innerJoinON(
				LayoutSetTable.INSTANCE,
				LayoutSetTable.INSTANCE.companyId.eq(
					LayoutTable.INSTANCE.companyId
				).and(
					LayoutSetTable.INSTANCE.groupId.eq(
						LayoutTable.INSTANCE.groupId)
				).and(
					LayoutSetTable.INSTANCE.privateLayout.eq(
						LayoutTable.INSTANCE.privateLayout)
				)
			).innerJoinON(
				LayoutSetPrototypeTable.INSTANCE,
				LayoutSetPrototypeTable.INSTANCE.companyId.eq(
					LayoutSetTable.INSTANCE.companyId
				).and(
					LayoutSetPrototypeTable.INSTANCE.uuid.eq(
						LayoutSetTable.INSTANCE.layoutSetPrototypeUuid)
				)
			).innerJoinON(
				GroupTable.INSTANCE,
				GroupTable.INSTANCE.companyId.eq(
					LayoutSetPrototypeTable.INSTANCE.companyId
				).and(
					GroupTable.INSTANCE.classPK.eq(
						LayoutSetPrototypeTable.INSTANCE.layoutSetPrototypeId)
				)
			).innerJoinON(
				tempLayoutTable,
				tempLayoutTable.companyId.eq(
					GroupTable.INSTANCE.companyId
				).and(
					tempLayoutTable.groupId.eq(GroupTable.INSTANCE.groupId)
				).and(
					tempLayoutTable.friendlyURL.eq(
						LayoutTable.INSTANCE.friendlyURL)
				)
			).where(
				LayoutTable.INSTANCE.groupId.eq(
					layoutSet.getGroupId()
				).and(
					LayoutTable.INSTANCE.system.eq(false)
				).and(
					LayoutTable.INSTANCE.layoutSetPrototypeLayoutERC.isNull()
				)
			));
	}

	@Override
	public List<Long> getDuplicatedFriendlyURLPlids(
			LayoutSetPrototype layoutSetPrototype)
		throws PortalException {

		LayoutTable tempLayoutTable = LayoutTable.INSTANCE.as(
			"tempLayoutTable");

		return _layoutLocalService.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				LayoutTable.INSTANCE.plid
			).from(
				LayoutTable.INSTANCE
			).innerJoinON(
				GroupTable.INSTANCE,
				GroupTable.INSTANCE.companyId.eq(
					LayoutTable.INSTANCE.companyId
				).and(
					GroupTable.INSTANCE.groupId.eq(LayoutTable.INSTANCE.groupId)
				)
			).innerJoinON(
				LayoutSetPrototypeTable.INSTANCE,
				LayoutSetPrototypeTable.INSTANCE.companyId.eq(
					GroupTable.INSTANCE.companyId
				).and(
					LayoutSetPrototypeTable.INSTANCE.layoutSetPrototypeId.eq(
						GroupTable.INSTANCE.classPK)
				)
			).innerJoinON(
				LayoutSetTable.INSTANCE,
				LayoutSetTable.INSTANCE.companyId.eq(
					LayoutSetPrototypeTable.INSTANCE.companyId
				).and(
					LayoutSetTable.INSTANCE.layoutSetPrototypeUuid.eq(
						LayoutSetPrototypeTable.INSTANCE.uuid)
				)
			).innerJoinON(
				tempLayoutTable,
				tempLayoutTable.companyId.eq(
					LayoutSetTable.INSTANCE.companyId
				).and(
					tempLayoutTable.groupId.eq(LayoutSetTable.INSTANCE.groupId)
				).and(
					tempLayoutTable.privateLayout.eq(
						LayoutSetTable.INSTANCE.privateLayout)
				).and(
					tempLayoutTable.friendlyURL.eq(
						LayoutTable.INSTANCE.friendlyURL)
				).and(
					tempLayoutTable.layoutSetPrototypeLayoutERC.isNull()
				)
			).where(
				LayoutTable.INSTANCE.groupId.eq(
					layoutSetPrototype.getGroupId()
				).and(
					LayoutTable.INSTANCE.system.eq(false)
				)
			));
	}

	@Override
	public boolean hasDuplicatedFriendlyURLs(
			String layoutExternalReferenceCode, long groupId,
			boolean privateLayout, String friendlyURL)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		if (group.isLayoutSetPrototype()) {
			long count = _getDuplicatedFriendlyURLSiteLayoutsCount(
				layoutExternalReferenceCode, group.getCompanyId(),
				group.getGroupId(), friendlyURL);

			if (count > 0) {
				return true;
			}

			return false;
		}

		return _hasDuplicatedFriendlyURLPrototypeLayout(
			layoutExternalReferenceCode, groupId, privateLayout, friendlyURL);
	}

	/**
	 * Checks the permissions necessary for resetting the layout. If sufficient,
	 * the layout is reset by calling {@link #_resetPrototype(Layout)}.
	 *
	 * @param layout the page being checked for sufficient permissions
	 */
	@Override
	public void resetPrototype(Layout layout) throws PortalException {
		_checkResetPrototypePermissions(layout.getGroup(), layout);

		_resetPrototype(layout);
	}

	/**
	 * Checks the permissions necessary for resetting the layout set. If
	 * sufficient, the layout set is reset by calling {@link
	 * #_resetPrototype(LayoutSet)}.
	 *
	 * @param layoutSet the site being checked for sufficient permissions
	 */
	@Override
	public void resetPrototype(LayoutSet layoutSet) throws PortalException {
		_checkResetPrototypePermissions(layoutSet.getGroup(), null);

		_resetPrototype(layoutSet);
	}

	/**
	 * Sets the number of failed merge attempts for the layout prototype to a
	 * new value.
	 *
	 * @param layoutPrototype the page template of the counter being updated
	 * @param newMergeFailCount the new value of the counter
	 */
	@Override
	public void setMergeFailCount(
			LayoutPrototype layoutPrototype, int newMergeFailCount)
		throws PortalException {

		Layout layoutPrototypeLayout = layoutPrototype.getLayout();

		boolean updateLayoutPrototypeLayout = false;

		UnicodeProperties prototypeTypeSettingsUnicodeProperties =
			layoutPrototypeLayout.getTypeSettingsProperties();

		if (newMergeFailCount == 0) {
			if (prototypeTypeSettingsUnicodeProperties.containsKey(
					Sites.MERGE_FAIL_COUNT)) {

				prototypeTypeSettingsUnicodeProperties.remove(
					Sites.MERGE_FAIL_COUNT);

				updateLayoutPrototypeLayout = true;
			}
		}
		else {
			prototypeTypeSettingsUnicodeProperties.setProperty(
				Sites.MERGE_FAIL_COUNT, String.valueOf(newMergeFailCount));

			updateLayoutPrototypeLayout = true;
		}

		if (updateLayoutPrototypeLayout) {
			_layoutService.updateLayout(
				layoutPrototypeLayout.getGroupId(),
				layoutPrototypeLayout.isPrivateLayout(),
				layoutPrototypeLayout.getLayoutId(),
				layoutPrototypeLayout.getTypeSettings());
		}
	}

	/**
	 * Sets the number of failed merge attempts for the layout set prototype to
	 * a new value.
	 *
	 * @param layoutSetPrototype the site template of the counter being updated
	 * @param newMergeFailCount the new value of the counter
	 */
	@Override
	public void setMergeFailCount(
			LayoutSetPrototype layoutSetPrototype, int newMergeFailCount)
		throws PortalException {

		LayoutSet layoutSetPrototypeLayoutSet =
			layoutSetPrototype.getLayoutSet();

		boolean updateLayoutSetPrototypeLayoutSet = false;

		UnicodeProperties layoutSetPrototypeSettingsUnicodeProperties =
			layoutSetPrototypeLayoutSet.getSettingsProperties();

		if (newMergeFailCount == 0) {
			if (layoutSetPrototypeSettingsUnicodeProperties.containsKey(
					Sites.MERGE_FAIL_COUNT)) {

				layoutSetPrototypeSettingsUnicodeProperties.remove(
					Sites.MERGE_FAIL_COUNT);

				updateLayoutSetPrototypeLayoutSet = true;
			}
		}
		else {
			layoutSetPrototypeSettingsUnicodeProperties.setProperty(
				Sites.MERGE_FAIL_COUNT, String.valueOf(newMergeFailCount));

			updateLayoutSetPrototypeLayoutSet = true;
		}

		if (updateLayoutSetPrototypeLayoutSet) {
			_layoutSetService.updateSettings(
				layoutSetPrototypeLayoutSet.getGroupId(),
				layoutSetPrototypeLayoutSet.isPrivateLayout(),
				layoutSetPrototypeLayoutSet.getSettings());
		}
	}

	/**
	 * Checks the permissions necessary for resetting the layout or site. If the
	 * permissions are not sufficient, a {@link PortalException} is thrown.
	 *
	 * @param group the site being checked for sufficient permissions
	 * @param layout the page being checked for sufficient permissions
	 *        (optionally <code>null</code>). If <code>null</code>, the
	 *        permissions are only checked for resetting the site.
	 */
	private void _checkResetPrototypePermissions(Group group, Layout layout)
		throws PortalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((layout != null) &&
			!_layoutPermission.contains(
				permissionChecker, layout, ActionKeys.UPDATE)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, layout.getName(), layout.getLayoutId(),
				ActionKeys.UPDATE);
		}
		else if (!GroupPermissionUtil.contains(
					permissionChecker, group, ActionKeys.UPDATE) &&
				 (!group.isUser() ||
				  (permissionChecker.getUserId() != group.getClassPK()))) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, group.getName(), group.getGroupId(),
				ActionKeys.UPDATE);
		}
	}

	private Layout _getDuplicatedFriendlyURLPrototypeLayout(Layout layout)
		throws PortalException {

		LayoutSet layoutSet = layout.getLayoutSet();

		if (!layoutSet.isLayoutSetPrototypeLinkActive()) {
			return null;
		}

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId());

		LayoutSet prototypeLayoutSet = layoutSetPrototype.getLayoutSet();

		LayoutFriendlyURL layoutFriendlyURL =
			_layoutFriendlyURLLocalService.fetchFirstLayoutFriendlyURL(
				prototypeLayoutSet.getGroupId(),
				prototypeLayoutSet.isPrivateLayout(), layout.getFriendlyURL());

		if (layoutFriendlyURL == null) {
			return null;
		}

		Layout foundLayout = _layoutLocalService.getLayout(
			layoutFriendlyURL.getPlid());

		String layoutSetPrototypeLayoutERC =
			layout.getLayoutSetPrototypeLayoutERC();

		if (Validator.isNotNull(layout.getLayoutSetPrototypeLayoutERC()) &&
			layoutSetPrototypeLayoutERC.equals(
				foundLayout.getExternalReferenceCode())) {

			return null;
		}

		return foundLayout;
	}

	private List<Layout> _getDuplicatedFriendlyURLSiteLayouts(Layout layout)
		throws PortalException {

		return _layoutLocalService.getLayouts(
			_layoutLocalService.dslQuery(
				DSLQueryFactoryUtil.selectDistinct(
					LayoutTable.INSTANCE.plid
				).from(
					LayoutTable.INSTANCE
				).innerJoinON(
					LayoutSetTable.INSTANCE,
					LayoutSetTable.INSTANCE.companyId.eq(
						LayoutTable.INSTANCE.companyId
					).and(
						LayoutSetTable.INSTANCE.groupId.eq(
							LayoutTable.INSTANCE.groupId)
					).and(
						LayoutSetTable.INSTANCE.privateLayout.eq(
							LayoutTable.INSTANCE.privateLayout)
					)
				).innerJoinON(
					LayoutSetPrototypeTable.INSTANCE,
					LayoutSetPrototypeTable.INSTANCE.companyId.eq(
						LayoutSetTable.INSTANCE.companyId
					).and(
						LayoutSetPrototypeTable.INSTANCE.uuid.eq(
							LayoutSetTable.INSTANCE.layoutSetPrototypeUuid)
					)
				).innerJoinON(
					GroupTable.INSTANCE,
					GroupTable.INSTANCE.companyId.eq(
						LayoutSetPrototypeTable.INSTANCE.companyId
					).and(
						GroupTable.INSTANCE.classPK.eq(
							LayoutSetPrototypeTable.INSTANCE.
								layoutSetPrototypeId)
					)
				).where(
					LayoutSetTable.INSTANCE.companyId.eq(
						layout.getCompanyId()
					).and(
						LayoutTable.INSTANCE.friendlyURL.eq(
							layout.getFriendlyURL())
					).and(
						LayoutTable.INSTANCE.layoutSetPrototypeLayoutERC.
							isNull()
					).and(
						GroupTable.INSTANCE.groupId.eq(layout.getGroupId())
					)
				)));
	}

	private long _getDuplicatedFriendlyURLSiteLayoutsCount(
			String layoutExternalReferenceCode, long companyId, long groupId,
			String friendlyURL)
		throws PortalException {

		Predicate layoutSetPrototypeLayoutERCPredicate =
			LayoutTable.INSTANCE.layoutSetPrototypeLayoutERC.isNull();

		if (Validator.isNotNull(layoutExternalReferenceCode)) {
			layoutSetPrototypeLayoutERCPredicate = Predicate.withParentheses(
				layoutSetPrototypeLayoutERCPredicate.or(
					LayoutTable.INSTANCE.layoutSetPrototypeLayoutERC.neq(
						layoutExternalReferenceCode)));
		}

		return _layoutLocalService.dslQuery(
			DSLQueryFactoryUtil.count(
			).from(
				LayoutTable.INSTANCE
			).innerJoinON(
				LayoutSetTable.INSTANCE,
				LayoutSetTable.INSTANCE.companyId.eq(
					LayoutTable.INSTANCE.companyId
				).and(
					LayoutSetTable.INSTANCE.groupId.eq(
						LayoutTable.INSTANCE.groupId)
				).and(
					LayoutSetTable.INSTANCE.privateLayout.eq(
						LayoutTable.INSTANCE.privateLayout)
				)
			).innerJoinON(
				LayoutSetPrototypeTable.INSTANCE,
				LayoutSetPrototypeTable.INSTANCE.companyId.eq(
					LayoutSetTable.INSTANCE.companyId
				).and(
					LayoutSetPrototypeTable.INSTANCE.uuid.eq(
						LayoutSetTable.INSTANCE.layoutSetPrototypeUuid)
				)
			).innerJoinON(
				GroupTable.INSTANCE,
				GroupTable.INSTANCE.companyId.eq(
					LayoutSetPrototypeTable.INSTANCE.companyId
				).and(
					GroupTable.INSTANCE.classPK.eq(
						LayoutSetPrototypeTable.INSTANCE.layoutSetPrototypeId)
				)
			).where(
				LayoutTable.INSTANCE.companyId.eq(
					companyId
				).and(
					GroupTable.INSTANCE.groupId.eq(groupId)
				).and(
					LayoutTable.INSTANCE.friendlyURL.eq(friendlyURL)
				).and(
					layoutSetPrototypeLayoutERCPredicate
				)
			));
	}

	private boolean _hasDuplicatedFriendlyURLPrototypeLayout(
			String layoutSetPrototypeLayoutERC, long groupId,
			boolean privateLayout, String friendlyURL)
		throws PortalException {

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			groupId, privateLayout);

		if (!layoutSet.isLayoutSetPrototypeLinkActive()) {
			return false;
		}

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId());

		LayoutSet prototypeLayoutSet = layoutSetPrototype.getLayoutSet();

		LayoutFriendlyURL layoutFriendlyURL =
			_layoutFriendlyURLLocalService.fetchFirstLayoutFriendlyURL(
				prototypeLayoutSet.getGroupId(),
				prototypeLayoutSet.isPrivateLayout(), friendlyURL);

		if (layoutFriendlyURL == null) {
			return false;
		}

		Layout foundLayout = _layoutLocalService.getLayout(
			layoutFriendlyURL.getPlid());

		if (Validator.isNotNull(layoutSetPrototypeLayoutERC) &&
			layoutSetPrototypeLayoutERC.equals(
				foundLayout.getExternalReferenceCode())) {

			return false;
		}

		return true;
	}

	/**
	 * Resets the modified timestamp on the layout, and then calls {@link
	 * #_resetPrototype(LayoutSet)} to reset the modified timestamp on the
	 * layout's site.
	 *
	 * <p>
	 * After the timestamps are reset, the modified page template and site
	 * template are merged into their linked layout and site when they are first
	 * accessed.
	 * </p>
	 *
	 * @param layout the page having its timestamp reset
	 */
	private void _resetPrototype(Layout layout) throws PortalException {
		layout.setModifiedDate(null);

		layout = _layoutLocalService.updateLayout(layout);

		_resetPrototype(layout.getLayoutSet());
	}

	/**
	 * Resets the modified timestamp on the layout set.
	 *
	 * <p>
	 * After the timestamp is reset, the modified site template is merged into
	 * its linked layout set when it is first accessed.
	 * </p>
	 *
	 * @param layoutSet the site having its timestamp reset
	 */
	private void _resetPrototype(LayoutSet layoutSet) throws PortalException {
		UnicodeProperties settingsUnicodeProperties =
			layoutSet.getSettingsProperties();

		settingsUnicodeProperties.remove(Sites.LAST_MERGE_TIME);

		settingsUnicodeProperties.setProperty(
			Sites.LAST_RESET_TIME, String.valueOf(System.currentTimeMillis()));

		_layoutSetLocalService.updateLayoutSet(layoutSet);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private LayoutSetService _layoutSetService;

}
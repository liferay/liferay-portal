/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.lar.DeletionSystemEventExporter;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.rest.dto.v1_0.Choice;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerBoolean;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Daniel Raposo
 */
public class PortletDataHandlerUtil {

	public static final String CHOICE_NAME_PRIVATE_PAGES = "private-pages";

	public static final String CONTROL_NAME_PAGES =
		com.liferay.exportimport.kernel.lar.PortletDataHandlerControl.
			getNamespacedName(PortletDataHandlerUtil._NAMESPACE, "pages");

	public static final String CONTROL_NAME_VISIBILITY =
		com.liferay.exportimport.kernel.lar.PortletDataHandlerControl.
			getNamespacedName(PortletDataHandlerUtil._NAMESPACE, "visibility");

	public static final String PORTLET_DATA_HANDLER_NAME_LAYOUT_SET =
		PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
			LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS;

	public static final String PORTLET_DATA_HANDLER_NAME_LOOK_AND_FEEL =
		"LOOK_AND_FEEL";

	public static long getAdditionCount(
		PortletDataHandler[] portletDataHandlers) {

		long additionCount = 0;

		for (PortletDataHandler portletDataHandler : portletDataHandlers) {
			additionCount += GetterUtil.getLong(
				portletDataHandler.getAdditionCount());
		}

		return additionCount;
	}

	public static long getDeletionCount(
		PortletDataHandler[] portletDataHandlers) {

		long deletionCount = 0;

		for (PortletDataHandler portletDataHandler : portletDataHandlers) {
			deletionCount += GetterUtil.getLong(
				portletDataHandler.getDeletionCount());
		}

		return deletionCount;
	}

	public static PortletDataHandler[] getExportPortletDataHandlers(
			DeletionSystemEventExporter deletionSystemEventExporter,
			Group group, Locale locale, Map<String, String[]> parameterMap,
			long plid, PortletDataContextFactory portletDataContextFactory,
			PortletDataHandlerProvider portletDataHandlerProvider,
			List<Portlet> portlets, boolean portletScoped, TimeZone timeZone)
		throws Exception {

		List<PortletDataHandler> portletDataHandlers = new ArrayList<>();

		long groupId = group.getGroupId();
		String range = MapUtil.getString(
			parameterMap, ExportImportDateUtil.RANGE);

		for (Portlet portlet : portlets) {
			com.liferay.exportimport.kernel.lar.PortletDataHandler
				portletDataHandler = portletDataHandlerProvider.provide(
					portlet);

			if (portletDataHandler == null) {
				continue;
			}

			boolean layoutSetScoped = _isLayoutSetScoped(portlet);

			if (!layoutSetScoped && portletDataHandler.isHidden()) {
				continue;
			}

			DateRange dateRange = ExportImportDateUtil.getDateRange(
				parameterMap, groupId, false, plid,
				layoutSetScoped ? null : portlet.getRootPortletId(), locale,
				timeZone);

			PortletDataContext portletDataContext =
				portletDataContextFactory.createPreparePortletDataContext(
					group.getCompanyId(), groupId, range,
					dateRange.getStartDate(), dateRange.getEndDate());

			portletDataHandler.prepareManifestSummary(portletDataContext);

			if (layoutSetScoped) {
				portletDataContext.addDeletionSystemEventStagedModelTypes(
					portletDataHandler.
						getDeletionSystemEventStagedModelTypes());

				long publicDeletionSystemEventsCount =
					deletionSystemEventExporter.getDeletionSystemEventsCount(
						portletDataContext);

				boolean privateLayoutsEnabled = _isPrivateLayoutsEnabled(
					group, portletScoped);

				long privateDeletionSystemEventsCount = 0;
				ManifestSummary privateManifestSummary = null;

				if (privateLayoutsEnabled) {
					DateRange privateDateRange =
						ExportImportDateUtil.getDateRange(
							parameterMap, groupId, true, plid, null, locale,
							timeZone);

					PortletDataContext privatePortletDataContext =
						portletDataContextFactory.
							createPreparePortletDataContext(
								group.getCompanyId(), groupId, range,
								privateDateRange.getStartDate(),
								privateDateRange.getEndDate());

					privatePortletDataContext.setPrivateLayout(true);

					portletDataHandler.prepareManifestSummary(
						privatePortletDataContext);

					privatePortletDataContext.
						addDeletionSystemEventStagedModelTypes(
							portletDataHandler.
								getDeletionSystemEventStagedModelTypes());

					privateDeletionSystemEventsCount =
						deletionSystemEventExporter.
							getDeletionSystemEventsCount(
								privatePortletDataContext);
					privateManifestSummary =
						privatePortletDataContext.getManifestSummary();
				}

				_addLayoutSetPortletDataHandler(
					group.getCompanyId(), locale, portlet, portletDataHandler,
					portletDataHandlers, privateDeletionSystemEventsCount,
					privateLayoutsEnabled, privateManifestSummary,
					publicDeletionSystemEventsCount,
					!group.isLayoutSetPrototype(),
					group.isLayoutSetPrototype() ? null :
						portletDataContext.getManifestSummary());
			}
			else {
				_addPortletDataHandler(
					group.getCompanyId(), locale,
					portletDataContext.getManifestSummary(), portlet,
					portletDataHandler,
					com.liferay.exportimport.kernel.lar.PortletDataHandler::
						getExportPortletDataHandlerControls,
					portletDataHandlers, portletScoped);

				if (portletScoped) {
					_addConfigurationPortletDataHandler(
						locale, portlet, portletDataHandlers,
						portletDataHandler.
							getExportConfigurationPortletDataHandlerControls(
								group.getCompanyId(), groupId, portlet, plid,
								false));
				}
			}
		}

		_addLookAndFeelPortletDataHandler(
			group, locale, portletDataHandlers, portletScoped);

		return portletDataHandlers.toArray(new PortletDataHandler[0]);
	}

	public static PortletDataHandler[] getExportPortletDataHandlers(
			Group group, Locale locale, long plid,
			PortletDataHandlerProvider portletDataHandlerProvider,
			List<Portlet> portlets, boolean portletScoped)
		throws Exception {

		List<PortletDataHandler> portletDataHandlers = new ArrayList<>();

		for (Portlet portlet : portlets) {
			com.liferay.exportimport.kernel.lar.PortletDataHandler
				portletDataHandler = portletDataHandlerProvider.provide(
					portlet);

			if (portletDataHandler == null) {
				continue;
			}

			if (_isLayoutSetScoped(portlet)) {
				_addLayoutSetPortletDataHandler(
					group.getCompanyId(), locale, portlet, portletDataHandler,
					portletDataHandlers, 0,
					_isPrivateLayoutsEnabled(group, portletScoped), null, 0,
					!group.isLayoutSetPrototype(), null);

				continue;
			}

			if (portletDataHandler.isHidden()) {
				continue;
			}

			_addPortletDataHandler(
				group.getCompanyId(), locale, null, portlet, portletDataHandler,
				com.liferay.exportimport.kernel.lar.PortletDataHandler::
					getExportPortletDataHandlerControls,
				portletDataHandlers, portletScoped);

			if (!portletScoped) {
				continue;
			}

			_addConfigurationPortletDataHandler(
				locale, portlet, portletDataHandlers,
				portletDataHandler.
					getExportConfigurationPortletDataHandlerControls(
						group.getCompanyId(), group.getGroupId(), portlet, plid,
						false));
		}

		_addLookAndFeelPortletDataHandler(
			group, locale, portletDataHandlers, portletScoped);

		return portletDataHandlers.toArray(new PortletDataHandler[0]);
	}

	public static PortletDataHandler[] getImportPortletDataHandlers(
			boolean counted, Group group, Locale locale,
			ManifestSummary manifestSummary, List<Portlet> portlets,
			boolean portletScoped)
		throws Exception {

		List<PortletDataHandler> portletDataHandlers = new ArrayList<>();

		for (Portlet portlet : portlets) {
			com.liferay.exportimport.kernel.lar.PortletDataHandler
				portletDataHandler = portlet.getPortletDataHandlerInstance();

			if (portletDataHandler == null) {
				continue;
			}

			if (_isLayoutSetScoped(portlet)) {
				_addLayoutSetPortletDataHandler(
					group.getCompanyId(), locale, portlet, portletDataHandler,
					portletDataHandlers, 0, false, null, 0, true,
					counted ? manifestSummary : null);

				continue;
			}

			if (portletDataHandler.isHidden()) {
				continue;
			}

			_addPortletDataHandler(
				group.getCompanyId(), locale, counted ? manifestSummary : null,
				portlet, portletDataHandler,
				com.liferay.exportimport.kernel.lar.PortletDataHandler::
					getImportPortletDataHandlerControls,
				portletDataHandlers, portletScoped);

			if (!portletScoped) {
				continue;
			}

			_addConfigurationPortletDataHandler(
				locale, portlet, portletDataHandlers,
				portletDataHandler.
					getImportConfigurationPortletDataHandlerControls(
						portlet, manifestSummary));
		}

		_addLookAndFeelPortletDataHandler(
			group, locale, portletDataHandlers, portletScoped);

		return portletDataHandlers.toArray(new PortletDataHandler[0]);
	}

	private static void _addConfigurationPortletDataHandler(
		Locale locale, Portlet portlet,
		List<PortletDataHandler> portletDataHandlers,
		com.liferay.exportimport.kernel.lar.PortletDataHandlerControl[]
			sourcePortletDataHandlerControls) {

		if (ArrayUtil.isEmpty(sourcePortletDataHandlerControls)) {
			return;
		}

		portletDataHandlers.add(
			new PortletDataHandler() {
				{
					setLabel(() -> PortalUtil.getPortletTitle(portlet, locale));
					setName(
						() ->
							PortletDataHandlerKeys.PORTLET_CONFIGURATION +
								StringPool.UNDERLINE +
									portlet.getRootPortletId());
					setPortletDataHandlerControls(
						() -> _toConfigurationPortletDataHandlerControls(
							locale, sourcePortletDataHandlerControls,
							portlet.getRootPortletId()));
					setSectionKey(
						() -> ExportImportConstants.SECTION_KEY_CONFIGURATION);
					setSectionLabel(
						() -> LanguageUtil.get(
							locale,
							ExportImportConstants.SECTION_KEY_CONFIGURATION));
				}
			});
	}

	private static void _addLayoutSetPortletDataHandler(
		long companyId, Locale locale, Portlet portlet,
		com.liferay.exportimport.kernel.lar.PortletDataHandler
			portletDataHandler,
		List<PortletDataHandler> portletDataHandlers,
		long privateLayoutDeletionCount, boolean privateLayoutsEnabled,
		ManifestSummary privateManifestSummary, long publicLayoutDeletionCount,
		boolean publicLayoutsEnabled, ManifestSummary publicManifestSummary) {

		if (!portletDataHandler.isEnabled(companyId)) {
			return;
		}

		long privateLayoutAdditionCount = _getExportModelCount(
			privateManifestSummary, portletDataHandler);
		long publicLayoutAdditionCount = _getExportModelCount(
			publicManifestSummary, portletDataHandler);

		if (((privateManifestSummary != null) ||
			 (publicManifestSummary != null)) &&
			(privateLayoutAdditionCount == 0) &&
			(privateLayoutDeletionCount == 0) &&
			(publicLayoutAdditionCount == 0) &&
			(publicLayoutDeletionCount == 0)) {

			return;
		}

		long layoutAdditionCount = publicLayoutAdditionCount;
		long layoutDeletionCount = publicLayoutDeletionCount;

		if (!publicLayoutsEnabled) {
			layoutAdditionCount = privateLayoutAdditionCount;
			layoutDeletionCount = privateLayoutDeletionCount;
		}

		long finalLayoutAdditionCount = layoutAdditionCount;
		long finalLayoutDeletionCount = layoutDeletionCount;

		String portletDataHandlerSectionKey = _getSectionKey(
			portletDataHandler, false);

		portletDataHandlers.add(
			new PortletDataHandler() {
				{
					setAdditionCount(() -> finalLayoutAdditionCount);
					setDeletionCount(() -> finalLayoutDeletionCount);
					setDescription(
						() -> portletDataHandler.getDescription(locale));
					setLabel(
						() -> {
							String portletTitle = portletDataHandler.getTitle(
								locale);

							if (portletTitle != null) {
								return portletTitle;
							}

							return PortalUtil.getPortletTitle(portlet, locale);
						});
					setName(() -> PORTLET_DATA_HANDLER_NAME_LAYOUT_SET);
					setPortletDataHandlerControls(
						() -> {
							PortletDataHandlerTreeSelection
								pagesPortletDataHandlerTreeSelection =
									_toPagesPortletDataHandlerTreeSelection(
										finalLayoutAdditionCount,
										finalLayoutDeletionCount, locale);

							if (!privateLayoutsEnabled) {
								return new PortletDataHandlerControl[] {
									pagesPortletDataHandlerTreeSelection
								};
							}

							return new PortletDataHandlerControl[] {
								_toVisibilityPortletDataHandlerChoice(
									locale,
									pagesPortletDataHandlerTreeSelection,
									privateLayoutAdditionCount,
									privateLayoutDeletionCount,
									publicLayoutAdditionCount,
									publicLayoutDeletionCount,
									publicLayoutsEnabled)
							};
						});
					setSectionKey(() -> portletDataHandlerSectionKey);
					setSectionLabel(
						() -> LanguageUtil.get(
							locale, portletDataHandlerSectionKey));
					setTag(() -> portletDataHandler.getTag(locale));
				}
			});
	}

	private static void _addLookAndFeelPortletDataHandler(
		Group group, Locale locale,
		List<PortletDataHandler> portletDataHandlers, boolean portletScoped) {

		if (portletScoped || !ScopeUtil.isLookAndFeelEnabled(group)) {
			return;
		}

		portletDataHandlers.add(
			new PortletDataHandler() {
				{
					setLabel(() -> LanguageUtil.get(locale, "look-and-feel"));
					setName(() -> PORTLET_DATA_HANDLER_NAME_LOOK_AND_FEEL);
					setPortletDataHandlerControls(
						() -> new PortletDataHandlerControl[] {
							_toLookAndFeelPortletDataHandlerBoolean(
								PortletDataHandlerKeys.THEME_REFERENCE,
								"theme-settings", locale),
							_toLookAndFeelPortletDataHandlerBoolean(
								PortletDataHandlerKeys.LOGO, "logo", locale),
							_toLookAndFeelPortletDataHandlerBoolean(
								PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
								"site-pages-settings", locale),
							_toLookAndFeelPortletDataHandlerBoolean(
								PortletDataHandlerKeys.
									LAYOUT_SET_PROTOTYPE_SETTINGS,
								"site-template-settings", locale)
						});
					setSectionKey(
						() -> ExportImportConstants.SECTION_KEY_SITE_BUILDER);
					setSectionLabel(
						() -> LanguageUtil.get(
							locale,
							ExportImportConstants.SECTION_KEY_SITE_BUILDER));
				}
			});
	}

	private static void _addPortletDataHandler(
			long companyId, Locale locale, ManifestSummary manifestSummary,
			Portlet portlet,
			com.liferay.exportimport.kernel.lar.PortletDataHandler
				portletDataHandler,
			UnsafeFunction
				<com.liferay.exportimport.kernel.lar.PortletDataHandler,
				 com.liferay.exportimport.kernel.lar.
					 PortletDataHandlerControl[],
				 Exception> portletDataHandlerControlsUnsafeFunction,
			List<PortletDataHandler> portletDataHandlers, boolean portletScoped)
		throws Exception {

		if (!portletDataHandler.isEnabled(companyId)) {
			return;
		}

		long modelAdditionCount = 0;
		long modelDeletionCount = 0;

		if (manifestSummary != null) {
			modelAdditionCount = _getExportModelCount(
				manifestSummary, portletDataHandler);
			modelDeletionCount = Math.max(
				0,
				manifestSummary.getModelDeletionCount(
					portletDataHandler.
						getDeletionSystemEventStagedModelTypes()));

			if ((modelAdditionCount == 0) && (modelDeletionCount == 0)) {
				return;
			}
		}

		long finalModelAdditionCount = modelAdditionCount;
		long finalModelDeletionCount = modelDeletionCount;

		String portletDataHandlerSectionKey = _getSectionKey(
			portletDataHandler, portletScoped);

		com.liferay.exportimport.kernel.lar.PortletDataHandlerControl[]
			sourcePortletDataHandlerControls =
				portletDataHandlerControlsUnsafeFunction.apply(
					portletDataHandler);

		portletDataHandlers.add(
			new PortletDataHandler() {
				{
					setAdditionCount(() -> finalModelAdditionCount);
					setDeletionCount(() -> finalModelDeletionCount);
					setDescription(
						() -> portletDataHandler.getDescription(locale));
					setLabel(
						() -> {
							String portletTitle = portletDataHandler.getTitle(
								locale);

							if (portletTitle != null) {
								return portletTitle;
							}

							return PortalUtil.getPortletTitle(portlet, locale);
						});
					setName(
						() ->
							PortletDataHandlerKeys.PORTLET_DATA +
								StringPool.UNDERLINE + portlet.getPortletId());
					setPortletDataHandlerControls(
						() -> _toPortletDataHandlerControls(
							locale, manifestSummary,
							sourcePortletDataHandlerControls));
					setSectionKey(() -> portletDataHandlerSectionKey);
					setSectionLabel(
						() -> LanguageUtil.get(
							locale, portletDataHandlerSectionKey));
					setTag(() -> portletDataHandler.getTag(locale));
				}
			});
	}

	private static long _getExportModelCount(
		ManifestSummary manifestSummary,
		com.liferay.exportimport.kernel.lar.PortletDataHandler
			portletDataHandler) {

		if (manifestSummary == null) {
			return 0;
		}

		return Math.max(
			0, portletDataHandler.getExportModelCount(manifestSummary));
	}

	private static String _getSectionKey(
		com.liferay.exportimport.kernel.lar.PortletDataHandler
			portletDataHandler,
		boolean portletScoped) {

		if (portletScoped) {
			return ExportImportConstants.SECTION_KEY_CONTENT;
		}

		return GetterUtil.getString(
			portletDataHandler.getSectionKey(),
			ExportImportConstants.SECTION_KEY_OTHER);
	}

	private static boolean _isLayoutSetScoped(Portlet portlet) {
		return LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS.equals(
			portlet.getPortletId());
	}

	private static boolean _isPrivateLayoutsEnabled(
		Group group, boolean portletScoped) {

		if (!portletScoped &&
			(group.isLayoutSetPrototype() || group.isPrivateLayoutsEnabled())) {

			return true;
		}

		return false;
	}

	private static PortletDataHandlerControl[]
		_toConfigurationPortletDataHandlerControls(
			Locale locale,
			com.liferay.exportimport.kernel.lar.PortletDataHandlerControl[]
				portletDataHandlerControls,
			String rootPortletId) {

		return TransformUtil.transform(
			portletDataHandlerControls,
			portletDataHandlerControl -> new PortletDataHandlerBoolean() {
				{
					setLabel(
						() -> LanguageUtil.get(
							locale, portletDataHandlerControl.getLabel()));
					setName(
						() ->
							portletDataHandlerControl.getName() +
								StringPool.UNDERLINE + rootPortletId);
					setSelected(() -> true);
					setType(() -> PortletDataHandlerControl.Type.BOOLEAN);
				}
			},
			PortletDataHandlerControl.class);
	}

	private static PortletDataHandlerBoolean
		_toLookAndFeelPortletDataHandlerBoolean(
			String controlName, String labelKey, Locale locale) {

		return new PortletDataHandlerBoolean() {
			{
				setLabel(() -> LanguageUtil.get(locale, labelKey));
				setName(() -> controlName);
				setSelected(() -> true);
				setType(() -> PortletDataHandlerControl.Type.BOOLEAN);
			}
		};
	}

	private static PortletDataHandlerTreeSelection
		_toPagesPortletDataHandlerTreeSelection(
			long layoutAdditionCount, long layoutDeletionCount, Locale locale) {

		return new PortletDataHandlerTreeSelection() {
			{
				setAdditionCount(() -> layoutAdditionCount);
				setDeletionCount(() -> layoutDeletionCount);
				setLabel(() -> LanguageUtil.get(locale, "pages"));
				setName(() -> CONTROL_NAME_PAGES);
				setSelected(() -> true);
				setType(() -> PortletDataHandlerControl.Type.TREE_SELECTION);
			}
		};
	}

	private static PortletDataHandlerControl _toPortletDataHandlerControl(
		Locale locale, ManifestSummary manifestSummary,
		com.liferay.exportimport.kernel.lar.PortletDataHandlerControl
			portletDataHandlerControl) {

		if (portletDataHandlerControl instanceof
				com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean
					portletDataHandlerBoolean) {

			if (portletDataHandlerBoolean.getClassName() == null) {
				return new PortletDataHandlerBoolean() {
					{
						setLabel(
							() -> LanguageUtil.get(
								locale, portletDataHandlerBoolean.getLabel()));
						setName(portletDataHandlerBoolean::getNamespacedName);
						setPortletDataHandlerControls(
							() -> _toPortletDataHandlerControls(
								locale, manifestSummary,
								portletDataHandlerBoolean.
									getChildrenPortletDataHandlerControls()));
						setSelected(portletDataHandlerBoolean::getDefaultState);
						setType(() -> PortletDataHandlerControl.Type.BOOLEAN);
					}
				};
			}

			long modelAdditionCount = 0;
			long modelDeletionCount = 0;

			if (manifestSummary != null) {
				StagedModelType stagedModelType = new StagedModelType(
					portletDataHandlerBoolean.getClassName(),
					portletDataHandlerBoolean.getReferrerClassName());

				modelAdditionCount = Math.max(
					0, manifestSummary.getModelAdditionCount(stagedModelType));
				modelDeletionCount = Math.max(
					0, manifestSummary.getModelDeletionCount(stagedModelType));

				if ((modelAdditionCount == 0) && (modelDeletionCount == 0)) {
					return null;
				}
			}

			long finalModelAdditionCount = modelAdditionCount;
			long finalModelDeletionCount = modelDeletionCount;

			return new PortletDataHandlerBoolean() {
				{
					setAdditionCount(() -> finalModelAdditionCount);
					setDeletionCount(() -> finalModelDeletionCount);
					setLabel(
						() -> LanguageUtil.get(
							locale, portletDataHandlerBoolean.getLabel()));
					setName(portletDataHandlerBoolean::getNamespacedName);
					setPortletDataHandlerControls(
						() -> _toPortletDataHandlerControls(
							locale, manifestSummary,
							portletDataHandlerBoolean.
								getChildrenPortletDataHandlerControls()));
					setSelected(portletDataHandlerBoolean::getDefaultState);
					setType(() -> PortletDataHandlerControl.Type.BOOLEAN);
				}
			};
		}

		if (portletDataHandlerControl instanceof
				com.liferay.exportimport.kernel.lar.PortletDataHandlerChoice
					portletDataHandlerChoice) {

			return new PortletDataHandlerChoice() {
				{
					setChoice(portletDataHandlerChoice::getDefaultChoice);
					setChoices(
						() -> TransformUtil.transform(
							portletDataHandlerChoice.getChoices(),
							choice -> new Choice() {
								{
									setLabel(
										() -> LanguageUtil.get(locale, choice));
									setName(() -> choice);
								}
							},
							Choice.class));
					setLabel(
						() -> LanguageUtil.get(
							locale, portletDataHandlerChoice.getLabel()));
					setName(portletDataHandlerChoice::getNamespacedName);
					setType(() -> PortletDataHandlerControl.Type.CHOICE);
				}
			};
		}

		return null;
	}

	private static PortletDataHandlerControl[] _toPortletDataHandlerControls(
		Locale locale, ManifestSummary manifestSummary,
		com.liferay.exportimport.kernel.lar.PortletDataHandlerControl[]
			portletDataHandlerControls) {

		if (ArrayUtil.isEmpty(portletDataHandlerControls)) {
			return null;
		}

		return TransformUtil.transform(
			portletDataHandlerControls,
			portletDataHandlerControl -> _toPortletDataHandlerControl(
				locale, manifestSummary, portletDataHandlerControl),
			PortletDataHandlerControl.class);
	}

	private static PortletDataHandlerChoice
		_toVisibilityPortletDataHandlerChoice(
			Locale locale,
			PortletDataHandlerTreeSelection
				pagesPortletDataHandlerTreeSelection,
			long privateLayoutAdditionCount, long privateLayoutDeletionCount,
			long publicLayoutAdditionCount, long publicLayoutDeletionCount,
			boolean publicLayoutsEnabled) {

		return new PortletDataHandlerChoice() {
			{
				setChoice(
					() -> {
						if (publicLayoutsEnabled) {
							return _CHOICE_NAME_PUBLIC_PAGES;
						}

						return CHOICE_NAME_PRIVATE_PAGES;
					});
				setChoices(
					() -> {
						List<Choice> choices = new ArrayList<>();

						if (publicLayoutsEnabled) {
							choices.add(
								new Choice() {
									{
										setAdditionCount(
											() -> publicLayoutAdditionCount);
										setDeletionCount(
											() -> publicLayoutDeletionCount);
										setLabel(
											() -> LanguageUtil.get(
												locale,
												_CHOICE_NAME_PUBLIC_PAGES));
										setName(
											() -> _CHOICE_NAME_PUBLIC_PAGES);
									}
								});
						}

						choices.add(
							new Choice() {
								{
									setAdditionCount(
										() -> privateLayoutAdditionCount);
									setDeletionCount(
										() -> privateLayoutDeletionCount);
									setLabel(
										() -> LanguageUtil.get(
											locale, CHOICE_NAME_PRIVATE_PAGES));
									setName(() -> CHOICE_NAME_PRIVATE_PAGES);
								}
							});

						return choices.toArray(new Choice[0]);
					});
				setLabel(() -> LanguageUtil.get(locale, "visibility"));
				setName(() -> CONTROL_NAME_VISIBILITY);
				setPortletDataHandlerControls(
					() -> new PortletDataHandlerControl[] {
						pagesPortletDataHandlerTreeSelection
					});
				setType(() -> PortletDataHandlerControl.Type.CHOICE);
			}
		};
	}

	private static final String _CHOICE_NAME_PUBLIC_PAGES = "public-pages";

	private static final String _NAMESPACE = "layout_set";

}
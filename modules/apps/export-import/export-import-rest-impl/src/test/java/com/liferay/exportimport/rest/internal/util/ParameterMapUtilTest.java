/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.rest.dto.v1_0.Choice;
import com.liferay.exportimport.rest.dto.v1_0.ExportProcessRequest;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerBoolean;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.exportimport.rest.dto.v1_0.PublishProcessRequest;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.BadRequestException;

import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 * @author Daniel Raposo
 */
public class ParameterMapUtilTest {

	@ClassRule
	@Rule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testToParameterMap() {
		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection =
			_toPortletDataHandlerTreeSelection(
				_CONTROL_NAME_PAGES, false, true);

		portletDataHandlerTreeSelection.
			setExcludedSubtreeExternalReferenceCodes(new String[] {"b"});
		portletDataHandlerTreeSelection.setItemExternalReferenceCodes(
			new String[] {"a"});

		Map<String, String[]> parameterMap = ParameterMapUtil.toParameterMap(
			_toExportProcessRequest(
				_toPortletDataHandler(
					_PORTLET_DATA_HANDLER_NAME, null,
					_toPortletDataHandlerBoolean(
						_CONTROL_NAME_BOOLEAN, true,
						_toPortletDataHandlerChoice("y", _CONTROL_NAME_CHOICE)),
					_toPortletDataHandlerChoice(
						"y", _CONTROL_NAME_VISIBILITY,
						portletDataHandlerTreeSelection))),
			_getAvailablePortletDataHandlers(), false);

		Assert.assertArrayEquals(
			new String[] {"true"}, parameterMap.get(_CONTROL_NAME_BOOLEAN));
		Assert.assertArrayEquals(
			new String[] {"y"}, parameterMap.get(_CONTROL_NAME_CHOICE));
		Assert.assertArrayEquals(
			new String[] {"true"}, parameterMap.get(_CONTROL_NAME_PAGES));
		Assert.assertArrayEquals(
			new String[] {"false"},
			parameterMap.get(_CONTROL_NAME_PAGES + "_all"));
		Assert.assertFalse(
			parameterMap.containsKey(_CONTROL_NAME_PAGES + "_excluded-items"));
		Assert.assertArrayEquals(
			new String[] {"b"},
			parameterMap.get(_CONTROL_NAME_PAGES + "_excluded-subtrees"));
		Assert.assertArrayEquals(
			new String[] {"a"},
			parameterMap.get(_CONTROL_NAME_PAGES + "_items"));
		Assert.assertFalse(
			parameterMap.containsKey(_CONTROL_NAME_PAGES + "_subtrees"));
		Assert.assertFalse(parameterMap.containsKey(_CONTROL_NAME_SETTING));
		Assert.assertArrayEquals(
			new String[] {"y"}, parameterMap.get(_CONTROL_NAME_VISIBILITY));
		Assert.assertArrayEquals(
			new String[] {"true"},
			parameterMap.get(_PORTLET_DATA_HANDLER_NAME));
	}

	@Test
	public void testToParameterMapWhenControlIsNotSelected() {
		Map<String, String[]> parameterMap = ParameterMapUtil.toParameterMap(
			_toExportProcessRequest(
				_toPortletDataHandler(
					_PORTLET_DATA_HANDLER_NAME, null,
					_toPortletDataHandlerBoolean(
						_CONTROL_NAME_BOOLEAN, false,
						_toPortletDataHandlerChoice("y", _CONTROL_NAME_CHOICE)),
					_toPortletDataHandlerChoice(
						"x", _CONTROL_NAME_VISIBILITY,
						_toPortletDataHandlerTreeSelection(
							_CONTROL_NAME_PAGES, false, false)))),
			_getAvailablePortletDataHandlers(), false);

		Assert.assertArrayEquals(
			new String[] {"false"}, parameterMap.get(_CONTROL_NAME_BOOLEAN));
		Assert.assertFalse(parameterMap.containsKey(_CONTROL_NAME_CHOICE));
		Assert.assertArrayEquals(
			new String[] {"false"}, parameterMap.get(_CONTROL_NAME_PAGES));
		Assert.assertFalse(
			parameterMap.containsKey(_CONTROL_NAME_PAGES + "_all"));
	}

	@Test
	public void testToParameterMapWhenControlsAreLeftOut() {
		Map<String, String[]> parameterMap = ParameterMapUtil.toParameterMap(
			_toExportProcessRequest(
				_toPortletDataHandler(_PORTLET_DATA_HANDLER_NAME, null)),
			_getAvailablePortletDataHandlers(), false);

		Assert.assertFalse(parameterMap.containsKey(_CONTROL_NAME_BOOLEAN));
		Assert.assertFalse(parameterMap.containsKey(_CONTROL_NAME_CHOICE));
		Assert.assertFalse(parameterMap.containsKey(_CONTROL_NAME_PAGES));
		Assert.assertFalse(parameterMap.containsKey(_CONTROL_NAME_SETTING));
		Assert.assertFalse(parameterMap.containsKey(_CONTROL_NAME_VISIBILITY));
		Assert.assertArrayEquals(
			new String[] {"true"},
			parameterMap.get(_PORTLET_DATA_HANDLER_NAME));
	}

	@Test
	public void testToParameterMapWhenLookAndFeelIsSelected() {
		Map<String, String[]> parameterMap = ParameterMapUtil.toParameterMap(
			_toExportProcessRequest(
				_toPortletDataHandler(
					PortletDataHandlerUtil.
						PORTLET_DATA_HANDLER_NAME_LOOK_AND_FEEL,
					null,
					_toPortletDataHandlerBoolean(
						PortletDataHandlerKeys.LOGO, true))),
			_getAvailablePortletDataHandlers(), false);

		Assert.assertFalse(
			parameterMap.containsKey(
				PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS));
		Assert.assertFalse(
			parameterMap.containsKey(
				PortletDataHandlerKeys.LAYOUT_SET_SETTINGS));
		Assert.assertArrayEquals(
			new String[] {"true"},
			parameterMap.get(PortletDataHandlerKeys.LOGO));
		Assert.assertFalse(
			parameterMap.containsKey(PortletDataHandlerKeys.THEME_REFERENCE));

		parameterMap = ParameterMapUtil.toParameterMap(
			_toExportProcessRequest(
				_toPortletDataHandler(_PORTLET_DATA_HANDLER_NAME, null)),
			_getAvailablePortletDataHandlers(), false);

		Assert.assertFalse(
			parameterMap.containsKey(PortletDataHandlerKeys.LOGO));
		Assert.assertFalse(
			parameterMap.containsKey(PortletDataHandlerKeys.THEME_REFERENCE));
	}

	@Test
	public void testToParameterMapWhenPortletDataHandlerIsNotSelected() {
		Map<String, String[]> parameterMap = ParameterMapUtil.toParameterMap(
			_toExportProcessRequest(
				_toPortletDataHandler(_PORTLET_DATA_HANDLER_NAME, false)),
			_getAvailablePortletDataHandlers(), false);

		Assert.assertFalse(parameterMap.containsKey(_CONTROL_NAME_BOOLEAN));
		Assert.assertFalse(
			parameterMap.containsKey(_PORTLET_DATA_HANDLER_NAME));
	}

	@Test
	public void testToParameterMapWhenRequestIsInvalid() {
		_assertBadRequest(
			"No portlet data handler exists with name \"unknown\"",
			_toPortletDataHandler("unknown", null));
		_assertBadRequest(
			"The choice \"z\" is not one of the choices of the control \"" +
				_CONTROL_NAME_CHOICE + "\"",
			_toPortletDataHandler(
				_PORTLET_DATA_HANDLER_NAME, null,
				_toPortletDataHandlerBoolean(
					_CONTROL_NAME_BOOLEAN, true,
					_toPortletDataHandlerChoice("z", _CONTROL_NAME_CHOICE))));
		_assertBadRequest(
			"The control \"" + _CONTROL_NAME_BOOLEAN +
				"\" is of type \"Boolean\", not \"Choice\"",
			_toPortletDataHandler(
				_PORTLET_DATA_HANDLER_NAME, null,
				_toPortletDataHandlerChoice("x", _CONTROL_NAME_BOOLEAN)));
		_assertBadRequest(
			StringBundler.concat(
				"No control exists with name \"", _CONTROL_NAME_CHOICE,
				"\" in \"", _PORTLET_DATA_HANDLER_NAME, "\""),
			_toPortletDataHandler(
				_PORTLET_DATA_HANDLER_NAME, null,
				_toPortletDataHandlerChoice("x", _CONTROL_NAME_CHOICE)));
		_assertBadRequest(
			StringBundler.concat(
				"No control exists with name \"", _CONTROL_NAME_PAGES,
				"\" in \"", _PORTLET_DATA_HANDLER_NAME, "\""),
			_toPortletDataHandler(
				_PORTLET_DATA_HANDLER_NAME, null,
				_toPortletDataHandlerTreeSelection(
					_CONTROL_NAME_PAGES, true, null)));
		_assertBadRequest(
			StringBundler.concat(
				"The control \"", _CONTROL_NAME_BOOLEAN,
				"\" is sent more than once in \"", _PORTLET_DATA_HANDLER_NAME,
				"\""),
			_toPortletDataHandler(
				_PORTLET_DATA_HANDLER_NAME, null,
				_toPortletDataHandlerBoolean(_CONTROL_NAME_BOOLEAN, true),
				_toPortletDataHandlerBoolean(_CONTROL_NAME_BOOLEAN, true)));
	}

	@Test
	public void testToPublishProcessRequest() {
		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			user.getTimeZone()
		).thenReturn(
			TimeZoneUtil.GMT
		);

		Date endDate = new Date(System.currentTimeMillis() + Time.DAY);
		Date startDate = new Date(
			System.currentTimeMillis() - (System.currentTimeMillis() % 1000));

		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection =
			_toPortletDataHandlerTreeSelection(
				_CONTROL_NAME_PAGES, false, true);

		portletDataHandlerTreeSelection.setSubtreeExternalReferenceCodes(
			new String[] {"a"});

		PublishProcessRequest publishProcessRequest =
			new PublishProcessRequest();

		publishProcessRequest.setComments(true);
		publishProcessRequest.setDateRangeType(
			PublishProcessRequest.DateRangeType.DATE_RANGE);
		publishProcessRequest.setEndDate(endDate);
		publishProcessRequest.setPortletDataHandlers(
			new PortletDataHandler[] {
				_toPortletDataHandler(
					_PORTLET_DATA_HANDLER_NAME, null,
					_toPortletDataHandlerBoolean(
						_CONTROL_NAME_BOOLEAN, true,
						_toPortletDataHandlerChoice("y", _CONTROL_NAME_CHOICE)),
					_toPortletDataHandlerChoice(
						"y", _CONTROL_NAME_VISIBILITY,
						portletDataHandlerTreeSelection))
			});
		publishProcessRequest.setStartDate(startDate);

		PortletDataHandler[] availablePortletDataHandlers =
			_getAvailablePortletDataHandlers();

		PublishProcessRequest reversedPublishProcessRequest =
			ParameterMapUtil.toPublishProcessRequest(
				availablePortletDataHandlers, LocaleUtil.US,
				ParameterMapUtil.putDateRangeParameters(
					publishProcessRequest.getDateRangeTypeAsString(),
					publishProcessRequest.getStartDate(),
					publishProcessRequest.getEndDate(),
					ParameterMapUtil.toParameterMap(
						publishProcessRequest, availablePortletDataHandlers),
					user),
				TimeZoneUtil.GMT);

		Assert.assertEquals(
			Boolean.TRUE, reversedPublishProcessRequest.getComments());
		Assert.assertEquals(
			PublishProcessRequest.DateRangeType.DATE_RANGE,
			reversedPublishProcessRequest.getDateRangeType());
		Assert.assertEquals(
			Boolean.FALSE, reversedPublishProcessRequest.getDeletions());
		Assert.assertEquals(
			_toSeconds(endDate),
			_toSeconds(reversedPublishProcessRequest.getEndDate()));
		Assert.assertEquals(
			startDate, reversedPublishProcessRequest.getStartDate());

		PortletDataHandler[] portletDataHandlers =
			reversedPublishProcessRequest.getPortletDataHandlers();

		Assert.assertEquals(
			portletDataHandlers.toString(), 1, portletDataHandlers.length);

		PortletDataHandler portletDataHandler = portletDataHandlers[0];

		Assert.assertEquals(
			_PORTLET_DATA_HANDLER_NAME, portletDataHandler.getName());

		PortletDataHandlerControl[] portletDataHandlerControls =
			portletDataHandler.getPortletDataHandlerControls();

		Assert.assertEquals(
			portletDataHandlerControls.toString(), 3,
			portletDataHandlerControls.length);

		PortletDataHandlerBoolean portletDataHandlerBoolean =
			(PortletDataHandlerBoolean)portletDataHandlerControls[0];

		Assert.assertEquals(
			_CONTROL_NAME_BOOLEAN, portletDataHandlerBoolean.getName());
		Assert.assertEquals(
			Boolean.TRUE, portletDataHandlerBoolean.getSelected());

		PortletDataHandlerControl[] nestedPortletDataHandlerControls =
			portletDataHandlerBoolean.getPortletDataHandlerControls();

		PortletDataHandlerChoice portletDataHandlerChoice =
			(PortletDataHandlerChoice)nestedPortletDataHandlerControls[0];

		Assert.assertEquals("y", portletDataHandlerChoice.getChoice());

		PortletDataHandlerBoolean settingPortletDataHandlerBoolean =
			(PortletDataHandlerBoolean)portletDataHandlerControls[1];

		Assert.assertEquals(
			Boolean.FALSE, settingPortletDataHandlerBoolean.getSelected());

		PortletDataHandlerChoice visibilityPortletDataHandlerChoice =
			(PortletDataHandlerChoice)portletDataHandlerControls[2];

		Assert.assertEquals(
			"y", visibilityPortletDataHandlerChoice.getChoice());

		nestedPortletDataHandlerControls =
			visibilityPortletDataHandlerChoice.getPortletDataHandlerControls();

		PortletDataHandlerTreeSelection
			reversedPortletDataHandlerTreeSelection =
				(PortletDataHandlerTreeSelection)
					nestedPortletDataHandlerControls[0];

		Assert.assertEquals(
			Boolean.FALSE,
			reversedPortletDataHandlerTreeSelection.getSelectAll());
		Assert.assertArrayEquals(
			new String[0],
			reversedPortletDataHandlerTreeSelection.
				getItemExternalReferenceCodes());
		Assert.assertEquals(
			Boolean.TRUE,
			reversedPortletDataHandlerTreeSelection.getSelected());
		Assert.assertArrayEquals(
			new String[] {"a"},
			reversedPortletDataHandlerTreeSelection.
				getSubtreeExternalReferenceCodes());
	}

	@Test
	public void testToPublishProcessRequestWhenDateRangeIsLast() {
		PublishProcessRequest publishProcessRequest =
			ParameterMapUtil.toPublishProcessRequest(
				_getAvailablePortletDataHandlers(), null,
				HashMapBuilder.put(
					ExportImportDateUtil.RANGE,
					new String[] {ExportImportDateUtil.RANGE_LAST}
				).put(
					"last", new String[] {"2"}
				).build(),
				null);

		Assert.assertEquals(
			PublishProcessRequest.DateRangeType.LAST,
			publishProcessRequest.getDateRangeType());

		Date startDate = publishProcessRequest.getStartDate();

		Assert.assertTrue(
			startDate.before(
				new Date(System.currentTimeMillis() - (2 * Time.HOUR) + 1000)));
		Assert.assertTrue(
			startDate.after(
				new Date(System.currentTimeMillis() - (2 * Time.HOUR) - 1000)));
	}

	private void _assertBadRequest(
		String message, PortletDataHandler portletDataHandler) {

		AssertUtils.assertFailure(
			BadRequestException.class, message,
			() -> ParameterMapUtil.toParameterMap(
				_toExportProcessRequest(portletDataHandler),
				_getAvailablePortletDataHandlers(), false));
	}

	private PortletDataHandler[] _getAvailablePortletDataHandlers() {
		return new PortletDataHandler[] {
			_toAvailablePortletDataHandler(),
			_toPortletDataHandler(
				PortletDataHandlerUtil.PORTLET_DATA_HANDLER_NAME_LOOK_AND_FEEL,
				null,
				_toPortletDataHandlerBoolean(
					PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS, true),
				_toPortletDataHandlerBoolean(
					PortletDataHandlerKeys.LAYOUT_SET_SETTINGS, true),
				_toPortletDataHandlerBoolean(PortletDataHandlerKeys.LOGO, true),
				_toPortletDataHandlerBoolean(
					PortletDataHandlerKeys.THEME_REFERENCE, true))
		};
	}

	private PortletDataHandler _toAvailablePortletDataHandler() {
		PortletDataHandlerChoice portletDataHandlerChoice =
			_toPortletDataHandlerChoice("x", _CONTROL_NAME_CHOICE);

		portletDataHandlerChoice.setChoices(
			new Choice[] {_toChoice("x"), _toChoice("y")});

		PortletDataHandlerBoolean portletDataHandlerBoolean =
			_toPortletDataHandlerBoolean(
				_CONTROL_NAME_BOOLEAN, true, portletDataHandlerChoice);

		PortletDataHandlerBoolean settingPortletDataHandlerBoolean =
			_toPortletDataHandlerBoolean(_CONTROL_NAME_SETTING, false);

		PortletDataHandlerChoice visibilityPortletDataHandlerChoice =
			_toPortletDataHandlerChoice(
				"x", _CONTROL_NAME_VISIBILITY,
				_toPortletDataHandlerTreeSelection(
					_CONTROL_NAME_PAGES, null, true));

		visibilityPortletDataHandlerChoice.setChoices(
			new Choice[] {_toChoice("x"), _toChoice("y")});

		return _toPortletDataHandler(
			_PORTLET_DATA_HANDLER_NAME, null, portletDataHandlerBoolean,
			settingPortletDataHandlerBoolean,
			visibilityPortletDataHandlerChoice);
	}

	private Choice _toChoice(String name) {
		Choice choice = new Choice();

		choice.setName(name);

		return choice;
	}

	private ExportProcessRequest _toExportProcessRequest(
		PortletDataHandler... portletDataHandlers) {

		ExportProcessRequest exportProcessRequest = new ExportProcessRequest();

		exportProcessRequest.setPortletDataHandlers(portletDataHandlers);

		return exportProcessRequest;
	}

	private PortletDataHandler _toPortletDataHandler(
		String name, Boolean selected,
		PortletDataHandlerControl... portletDataHandlerControls) {

		PortletDataHandler portletDataHandler = new PortletDataHandler();

		portletDataHandler.setName(name);

		if (portletDataHandlerControls.length > 0) {
			portletDataHandler.setPortletDataHandlerControls(
				portletDataHandlerControls);
		}

		portletDataHandler.setSelected(selected);

		return portletDataHandler;
	}

	private PortletDataHandlerBoolean _toPortletDataHandlerBoolean(
		String name, Boolean selected,
		PortletDataHandlerControl... portletDataHandlerControls) {

		PortletDataHandlerBoolean portletDataHandlerBoolean =
			new PortletDataHandlerBoolean();

		portletDataHandlerBoolean.setName(name);

		if (portletDataHandlerControls.length > 0) {
			portletDataHandlerBoolean.setPortletDataHandlerControls(
				portletDataHandlerControls);
		}

		portletDataHandlerBoolean.setSelected(selected);
		portletDataHandlerBoolean.setType(
			PortletDataHandlerControl.Type.BOOLEAN);

		return portletDataHandlerBoolean;
	}

	private PortletDataHandlerChoice _toPortletDataHandlerChoice(
		String choice, String name,
		PortletDataHandlerControl... portletDataHandlerControls) {

		PortletDataHandlerChoice portletDataHandlerChoice =
			new PortletDataHandlerChoice();

		portletDataHandlerChoice.setChoice(choice);
		portletDataHandlerChoice.setName(name);

		if (portletDataHandlerControls.length > 0) {
			portletDataHandlerChoice.setPortletDataHandlerControls(
				portletDataHandlerControls);
		}

		portletDataHandlerChoice.setType(PortletDataHandlerControl.Type.CHOICE);

		return portletDataHandlerChoice;
	}

	private PortletDataHandlerTreeSelection _toPortletDataHandlerTreeSelection(
		String name, Boolean selectAll, Boolean selected) {

		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection =
			new PortletDataHandlerTreeSelection();

		portletDataHandlerTreeSelection.setName(name);
		portletDataHandlerTreeSelection.setSelectAll(selectAll);
		portletDataHandlerTreeSelection.setSelected(selected);
		portletDataHandlerTreeSelection.setType(
			PortletDataHandlerControl.Type.TREE_SELECTION);

		return portletDataHandlerTreeSelection;
	}

	private long _toSeconds(Date date) {
		return date.getTime() / 1000;
	}

	private static final String _CONTROL_NAME_BOOLEAN =
		"_" + RandomTestUtil.randomString() + "_boolean";

	private static final String _CONTROL_NAME_CHOICE =
		"_" + RandomTestUtil.randomString() + "_choice";

	private static final String _CONTROL_NAME_PAGES =
		"_" + RandomTestUtil.randomString() + "_pages";

	private static final String _CONTROL_NAME_SETTING =
		"_" + RandomTestUtil.randomString() + "_setting";

	private static final String _CONTROL_NAME_VISIBILITY =
		"_" + RandomTestUtil.randomString() + "_visibility";

	private static final String _PORTLET_DATA_HANDLER_NAME =
		"PORTLET_DATA_" + RandomTestUtil.randomString();

}
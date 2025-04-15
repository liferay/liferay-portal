/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gogo.shell.web.internal.portlet;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.gogo.shell.web.internal.constants.GogoShellPortletKeys;
import com.liferay.gogo.shell.web.internal.constants.GogoShellWebKeys;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.TransientValue;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Converter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Gregory Amerson
 * @author David Truong
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-gogo-shell",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.render-weight=50",
		"jakarta.portlet.display-name=Gogo Shell",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + GogoShellPortletKeys.GOGO_SHELL,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class GogoShellPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		_initCommandSession(renderRequest);

		CommandSession commandSession = _getSessionAttribute(
			renderRequest, GogoShellWebKeys.COMMAND_SESSION);

		SessionMessages.add(
			renderRequest, "prompt", commandSession.get("prompt"));

		super.doView(renderRequest, renderResponse);
	}

	public void executeCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		CaptchaUtil.check(actionRequest);

		String command = ParamUtil.getString(actionRequest, "command");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_initCommandSession(actionRequest);

		CommandSession commandSession = _getSessionAttribute(
			actionRequest, GogoShellWebKeys.COMMAND_SESSION);

		UnsyncByteArrayOutputStream outputUnsyncByteArrayOutputStream =
			_getSessionAttribute(
				actionRequest, GogoShellWebKeys.COMMAND_SESSION_OUTPUT_STREAM);
		UnsyncByteArrayOutputStream errorUnsyncByteArrayOutputStream =
			_getSessionAttribute(
				actionRequest, GogoShellWebKeys.COMMAND_SESSION_ERROR_STREAM);
		PrintStream outputPrintStream = _getSessionAttribute(
			actionRequest,
			GogoShellWebKeys.COMMAND_SESSION_OUTPUT_PRINT_STREAM);
		PrintStream errorPrintStream = _getSessionAttribute(
			actionRequest, GogoShellWebKeys.COMMAND_SESSION_ERROR_PRINT_STREAM);

		try {
			SessionMessages.add(actionRequest, "command", command);

			_checkCommand(command, themeDisplay);

			Object result = commandSession.execute(command);

			if (result != null) {
				outputPrintStream.print(
					commandSession.format(result, Converter.INSPECT));
			}

			errorPrintStream.flush();
			outputPrintStream.flush();

			SessionMessages.add(
				actionRequest, "commandOutput",
				outputUnsyncByteArrayOutputStream.toString());

			String errorContent = errorUnsyncByteArrayOutputStream.toString();

			if (Validator.isNotNull(errorContent)) {
				throw new Exception(errorContent);
			}

			String successMessage = ParamUtil.getString(
				actionRequest, "successMessage");

			SessionMessages.add(
				actionRequest, "requestProcessed", successMessage);

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			hideDefaultErrorMessage(actionRequest);

			SessionErrors.add(actionRequest, "gogo", exception);
		}
		finally {
			outputUnsyncByteArrayOutputStream.reset();
			errorUnsyncByteArrayOutputStream.reset();
		}
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		_checkOmniadmin();

		super.processAction(actionRequest, actionResponse);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		_checkOmniadmin();

		super.render(renderRequest, renderResponse);
	}

	private void _checkCommand(String command, ThemeDisplay themeDisplay)
		throws Exception {

		Matcher matcher = _pattern.matcher(command);

		if (matcher.find()) {
			throw new Exception(
				_language.format(
					themeDisplay.getLocale(), "the-command-x-is-not-supported",
					command));
		}
	}

	private void _checkOmniadmin() throws PortletException {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			PrincipalException principalException =
				new PrincipalException.MustBeOmniadmin(permissionChecker);

			throw new PortletException(principalException);
		}
	}

	private <T> T _getSessionAttribute(
		PortletRequest portletRequest, String name) {

		PortletSession portletSession = portletRequest.getPortletSession();

		Object sessionAttribute = portletSession.getAttribute(name);

		if (!(sessionAttribute instanceof TransientValue)) {
			return null;
		}

		TransientValue<T> transientValue = (TransientValue<T>)sessionAttribute;

		return transientValue.getValue();
	}

	private void _initCommandSession(PortletRequest portletRequest) {
		PortletSession portletSession = portletRequest.getPortletSession();

		Object commandSessionAttribute = portletSession.getAttribute(
			GogoShellWebKeys.COMMAND_SESSION);

		if (commandSessionAttribute instanceof CommandSession) {
			return;
		}

		UnsyncByteArrayOutputStream outputUnsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();
		UnsyncByteArrayOutputStream errorUnsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		PrintStream outputPrintStream = new PrintStream(
			outputUnsyncByteArrayOutputStream);
		PrintStream errorPrintStream = new PrintStream(
			errorUnsyncByteArrayOutputStream);

		CommandSession commandSession = _commandProcessor.createSession(
			_emptyInputStream, outputPrintStream, errorPrintStream);

		commandSession.put("prompt", "g!");

		portletSession.setAttribute(
			GogoShellWebKeys.COMMAND_SESSION,
			new TransientValue<>(commandSession));

		portletSession.setAttribute(
			GogoShellWebKeys.COMMAND_SESSION_ERROR_PRINT_STREAM,
			new TransientValue<>(errorPrintStream));
		portletSession.setAttribute(
			GogoShellWebKeys.COMMAND_SESSION_ERROR_STREAM,
			new TransientValue<>(errorUnsyncByteArrayOutputStream));
		portletSession.setAttribute(
			GogoShellWebKeys.COMMAND_SESSION_OUTPUT_PRINT_STREAM,
			new TransientValue<>(outputPrintStream));
		portletSession.setAttribute(
			GogoShellWebKeys.COMMAND_SESSION_OUTPUT_STREAM,
			new TransientValue<>(outputUnsyncByteArrayOutputStream));
	}

	private static final InputStream _emptyInputStream =
		new UnsyncByteArrayInputStream(new byte[0]);
	private static final Pattern _pattern = Pattern.compile(
		".*(close|disconnect|exit|shutdown).*",
		Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	@Reference
	private CommandProcessor _commandProcessor;

	@Reference
	private Language _language;

}
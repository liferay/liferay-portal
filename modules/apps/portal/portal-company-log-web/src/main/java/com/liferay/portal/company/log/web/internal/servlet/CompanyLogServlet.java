/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.company.log.web.internal.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.log4j.Log4JUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Hai Yu
 */
@Component(
	enabled = false,
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.portal.company.log.web.internal.servlet.CompanyLogServlet",
		"osgi.http.whiteboard.servlet.pattern=/company-log/*",
		"servlet.init.httpMethods=GET"
	},
	service = Servlet.class
)
public class CompanyLogServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			String path = HttpComponentsUtil.fixPath(
				httpServletRequest.getPathInfo());

			String[] pathArray = StringUtil.split(path, CharPool.SLASH);

			if (pathArray.length == 0) {
				_list(httpServletRequest, httpServletResponse);
			}
			else if (pathArray.length == 2) {
				long companyId = GetterUtil.getLongStrict(pathArray[0]);

				Company company = _companyLocalService.fetchCompanyById(
					companyId);

				if (company == null) {
					return;
				}

				PermissionChecker permissionChecker = _getPermissionChecker(
					httpServletRequest);

				if (!permissionChecker.isCompanyAdmin(companyId)) {
					throw new PrincipalException.MustBeCompanyAdmin(
						permissionChecker.getUserId());
				}

				String fileName = pathArray[1];

				File file = _getFile(companyId, fileName);

				String action = ParamUtil.getString(
					httpServletRequest, "action");

				if (Validator.isNotNull(action) && action.equals("read")) {
					_read(httpServletResponse, file);

					return;
				}

				_download(httpServletRequest, httpServletResponse, file);
			}
		}
		catch (FileNotFoundException fileNotFoundException) {
			if (_log.isWarnEnabled()) {
				_log.warn(fileNotFoundException);
			}

			_portal.sendError(
				HttpServletResponse.SC_NOT_FOUND, fileNotFoundException,
				httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);
		}
	}

	private void _download(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, File file)
		throws Exception {

		String fileName = file.getName();

		String startString = ParamUtil.getString(httpServletRequest, "start");
		String endString = ParamUtil.getString(httpServletRequest, "end");

		if (Validator.isNull(startString) && Validator.isNull(endString)) {
			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse, fileName,
				Files.newInputStream(file.toPath()), file.length(),
				_mimeTypes.getContentType(fileName),
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		else {
			long start = 0;

			if (Validator.isNotNull(startString)) {
				start = GetterUtil.getLongStrict(startString);
			}

			long end = file.length();

			if (Validator.isNotNull(endString) &&
				(GetterUtil.getLongStrict(endString) < end)) {

				end = GetterUtil.getLongStrict(endString);
			}

			if ((start < 0) || (end < 0) || (start >= end)) {
				throw new IllegalArgumentException(
					"Start and end cannot be less than 0. Start cannot be " +
						"greater than or equal to end.");
			}

			if (start != 0) {
				--start;
			}

			try (FileChannel fileChannel = FileChannel.open(file.toPath())) {
				fileChannel.position(start);

				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse, fileName,
					Channels.newInputStream(fileChannel), end - start,
					_mimeTypes.getContentType(fileName),
					HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
			}
		}
	}

	private File _getFile(long companyId, String fileName) throws Exception {
		File companyLogDirectory = Log4JUtil.getCompanyLogDirectory(companyId);

		Path path = Paths.get(companyLogDirectory.getPath(), fileName);

		path = path.normalize();

		if (!path.startsWith(companyLogDirectory.getPath())) {
			throw new PrincipalException("Invalid path " + path);
		}

		File file = path.toFile();

		if (!file.exists()) {
			throw new FileNotFoundException(
				StringBundler.concat(
					"Unable to get file ", fileName, " for company ",
					companyId));
		}

		return file;
	}

	private PermissionChecker _getPermissionChecker(
			HttpServletRequest httpServletRequest)
		throws Exception {

		User user = _portal.getUser(httpServletRequest);

		if (user == null) {
			throw new PrincipalException.MustBeAuthenticated(0);
		}

		return _permissionCheckerFactory.create(user);
	}

	private void _list(
			Company company, JSONArray jsonArray,
			HttpServletRequest httpServletRequest)
		throws Exception {

		jsonArray.put(
			JSONUtil.put(
				"companyId", company.getCompanyId()
			).put(
				"companyLogs",
				() -> {
					File companyLogDirectory = Log4JUtil.getCompanyLogDirectory(
						company.getCompanyId());

					File[] files = companyLogDirectory.listFiles();

					Arrays.sort(files, Collections.reverseOrder());

					return JSONUtil.toJSONArray(
						files,
						file -> JSONUtil.put(
							"fileName", file.getName()
						).put(
							"fileSize",
							_language.formatStorageSize(
								file.length(), httpServletRequest.getLocale())
						));
				}
			).put(
				"webId", company.getWebId()
			));
	}

	private void _list(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		PermissionChecker permissionChecker = _getPermissionChecker(
			httpServletRequest);

		if (permissionChecker.isOmniadmin()) {
			_companyLocalService.forEachCompany(
				company -> _list(company, jsonArray, httpServletRequest));
		}
		else if (permissionChecker.isCompanyAdmin()) {
			User user = permissionChecker.getUser();

			_list(
				_companyLocalService.getCompany(user.getCompanyId()), jsonArray,
				httpServletRequest);
		}
		else {
			throw new PrincipalException.MustBeCompanyAdmin(
				permissionChecker.getUserId());
		}

		ServletResponseUtil.write(httpServletResponse, jsonArray.toString());
	}

	private void _read(HttpServletResponse httpServletResponse, File file)
		throws IOException {

		try (Scanner scanner = new Scanner(file)) {
			StringBuilder sb = new StringBuilder();

			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine() + "\n");
			}

			ServletResponseUtil.write(httpServletResponse, sb.toString());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyLogServlet.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private MimeTypes _mimeTypes;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

}
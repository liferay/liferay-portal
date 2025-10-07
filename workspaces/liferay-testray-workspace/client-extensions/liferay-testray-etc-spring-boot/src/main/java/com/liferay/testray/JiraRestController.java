/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.testray;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.testray.service.JiraOAuthService;
import com.liferay.testray.service.JiraService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Nilton Vieira
 */
@CrossOrigin("*")
@RequestMapping("/jira")
@RestController
public class JiraRestController extends BaseRestController {

	@GetMapping("/oauth/callback")
	public void getOAuthCallback(
			@RequestParam String code, @RequestParam String state,
			HttpSession httpSession, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (!StringUtil.equals(
				state,
				GetterUtil.getString(httpSession.getAttribute("state")))) {

			return;
		}

		_jiraOAuthService.generateToken(
			code, "code",
			httpServletRequest.getRequestURL(
			).toString());

		httpServletResponse.sendRedirect(
			UriComponentsBuilder.fromUriString(
				lxcDXPServerProtocol + "://" + lxcDXPMainDomain
			).build(
			).toString());
	}

	@GetMapping("/oauth/login")
	public void getOAuthLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String state = UUID.randomUUID(
		).toString();

		httpServletRequest.getSession(
		).setAttribute(
			"state", state
		);

		httpServletResponse.sendRedirect(
			UriComponentsBuilder.fromUriString(
				"https://auth.atlassian.com/authorize"
			).queryParam(
				"audience", "api.atlassian.com"
			).queryParam(
				"client_id", "{clientId}"
			).queryParam(
				"prompt", "consent"
			).queryParam(
				"redirect_uri",
				StringUtil.replace(
					httpServletRequest.getRequestURL(
					).toString(),
					"login", "callback")
			).queryParam(
				"response_type", "code"
			).queryParam(
				"scope", "offline_access read:jira-work write:jira-work"
			).queryParam(
				"state", state
			).build(
				_liferayTestrayJiraOAuthClientId
			).toString());
	}

	@PostMapping("issues/sync")
	@ResponseBody
	public ResponseEntity<Object> postIssueSync() {
		_jiraService.syncJiraIssues();

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("issues/{issueKey}")
	@ResponseBody
	public ResponseEntity<Object> putIssue(
		@PathVariable String issueKey, @RequestBody String json) {

		return new ResponseEntity<>(
			_jiraService.updateJiraIssue(issueKey, json), HttpStatus.OK);
	}

	@Value("${com.liferay.lxc.dxp.mainDomain}")
	protected String lxcDXPMainDomain;

	@Value("${com.liferay.lxc.dxp.server.protocol}")
	protected String lxcDXPServerProtocol;

	@Autowired
	private JiraOAuthService _jiraOAuthService;

	@Autowired
	private JiraService _jiraService;

	@Value("${liferay.testray.jira.oauth.client.id}")
	private String _liferayTestrayJiraOAuthClientId;

}
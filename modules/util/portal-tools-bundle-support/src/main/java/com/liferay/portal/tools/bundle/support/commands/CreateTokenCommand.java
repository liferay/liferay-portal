/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.bundle.support.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import com.liferay.portal.tools.bundle.support.constants.BundleSupportConstants;
import com.liferay.portal.tools.bundle.support.internal.util.FileUtil;
import com.liferay.portal.tools.bundle.support.internal.util.HttpUtil;

import java.io.Console;
import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author David Truong
 * @author Andrea Di Giorgi
 */
@Parameters(
	commandDescription = "Create a liferay.com download token.",
	commandNames = "createToken"
)
public class CreateTokenCommand implements Command {

	@Override
	public void execute() throws Exception {
		if (_tokenFile.exists() && !isForce()) {
			System.out.println(
				"The liferay.com download token already exists.");

			return;
		}

		Console console = System.console();

		while ((_emailAddress == null) || _emailAddress.isEmpty()) {
			_emailAddress = console.readLine("Email Address: ");
		}

		if ((_passwordFile != null) && _passwordFile.exists()) {
			_password = FileUtil.read(_passwordFile);
		}
		else {
			while ((_password == null) || _password.isEmpty()) {
				char[] characters = console.readPassword("Password: ");

				if (characters == null) {
					continue;
				}

				_password = new String(characters);
			}
		}

		String token = HttpUtil.createToken(
			_tokenURL.toURI(), _emailAddress, _password);

		Path tokenPath = _tokenFile.toPath();

		Path dirPath = tokenPath.getParent();

		if (dirPath != null) {
			Files.createDirectories(dirPath);
		}

		Files.write(tokenPath, token.getBytes(StandardCharsets.UTF_8));
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public String getPassword() {
		return _password;
	}

	public File getPasswordFile() {
		return _passwordFile;
	}

	public File getTokenFile() {
		return _tokenFile;
	}

	public URL getTokenUrl() {
		return _tokenURL;
	}

	public boolean isForce() {
		return _force;
	}

	public void setEmailAddress(String emailAddress) {
		_emailAddress = emailAddress;
	}

	public void setForce(boolean force) {
		_force = force;
	}

	public void setPassword(String password) {
		_password = password;
	}

	public void setPasswordFile(File passwordFile) {
		_passwordFile = passwordFile;
	}

	public void setTokenFile(File tokenFile) {
		_tokenFile = tokenFile;
	}

	public void setTokenUrl(URL tokenURL) {
		_tokenURL = tokenURL;
	}

	private static final URL _DEFAULT_TOKEN_URL;

	static {
		try {
			_DEFAULT_TOKEN_URL = new URL(
				BundleSupportConstants.DEFAULT_TOKEN_URL);
		}
		catch (MalformedURLException malformedURLException) {
			throw new ExceptionInInitializerError(malformedURLException);
		}
	}

	@Parameter(
		description = "Your liferay.com email address.",
		names = {"-e", "--email"}
	)
	private String _emailAddress;

	@Parameter(
		description = "Force creation of liferay.com download token.",
		names = {"-f", "--force"}
	)
	private boolean _force;

	@Parameter(
		description = "Your liferay.com password.", names = {"-p", "--password"}
	)
	private String _password;

	@Parameter(
		description = "The file where to read the password value.",
		names = "--password-file"
	)
	private File _passwordFile;

	@Parameter(
		description = "The file where to store your liferay.com download token.",
		names = "--token-file"
	)
	private File _tokenFile = BundleSupportConstants.DEFAULT_TOKEN_FILE;

	private URL _tokenURL = _DEFAULT_TOKEN_URL;

}
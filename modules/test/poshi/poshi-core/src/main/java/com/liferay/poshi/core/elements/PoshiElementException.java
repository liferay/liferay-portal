/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core.elements;

import com.liferay.poshi.core.PoshiGetterUtil;
import com.liferay.poshi.core.PoshiProperties;
import com.liferay.poshi.core.util.StringUtil;
import com.liferay.poshi.core.util.Validator;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Calum Ragan
 */
public class PoshiElementException extends Exception {

	public static List<Exception> getFilteredExceptions(
		List<Exception> exceptions) {

		List<Exception> filteredExceptions = new ArrayList<>();

		for (Exception exception : exceptions) {
			if (exception instanceof PoshiElementException) {
				PoshiElementException poshiElementException =
					(PoshiElementException)exception;

				if (!poshiElementException.isWarning()) {
					filteredExceptions.add(poshiElementException);
				}

				continue;
			}

			filteredExceptions.add(exception);
		}

		return filteredExceptions;
	}

	public static PoshiElement getRootPoshiElement(PoshiNode<?, ?> poshiNode) {
		if (Validator.isNotNull(poshiNode.getParent())) {
			PoshiElement parentPoshiElement =
				(PoshiElement)poshiNode.getParent();

			return getRootPoshiElement(parentPoshiElement);
		}

		return (PoshiElement)poshiNode;
	}

	public static List<Exception> getWarnings(List<Exception> exceptions) {
		List<Exception> warnings = new ArrayList<>();

		for (Exception exception : exceptions) {
			if (!(exception instanceof PoshiElementException)) {
				continue;
			}

			PoshiElementException poshiElementException =
				(PoshiElementException)exception;

			if (!poshiElementException.isWarning()) {
				continue;
			}

			warnings.add(poshiElementException);
		}

		return warnings;
	}

	public static String join(Object... objects) {
		StringBuilder sb = new StringBuilder();

		for (Object object : objects) {
			sb.append(object.toString());
		}

		return sb.toString();
	}

	public PoshiElementException(
		PoshiNode<?, ?> poshiNode, Object... messageParts) {

		this(join(messageParts), poshiNode);
	}

	public PoshiElementException(
		String msg, int errorLineNumber, String filePath,
		PoshiNode<?, ?> poshiNode) {

		this(msg);

		setErrorLineNumber(errorLineNumber);
		setFilePath(filePath);
		setPoshiNode(poshiNode);
	}

	public PoshiElementException(
		String msg, int errorLineNumber, String errorSnippet, URL filePathURL) {

		this(msg);

		setErrorLineNumber(errorLineNumber);
		setErrorSnippet(errorSnippet);
		setFilePath(filePathURL.getPath());
	}

	public PoshiElementException(String msg, PoshiNode<?, ?> poshiNode) {
		this(
			msg, poshiNode.getPoshiScriptLineNumber(), getFilePath(poshiNode),
			poshiNode);
	}

	public int getErrorLineNumber() {
		return _errorLineNumber;
	}

	public String getErrorSnippet() {
		if ((_errorSnippet == null) && !_errorSnippet.isEmpty()) {
			return _errorSnippet;
		}

		PoshiElement rootPoshiElement = getRootPoshiElement(getPoshiNode());

		int errorLineNumber = getErrorLineNumber();

		int startingLineNumber = Math.max(
			errorLineNumber - _ERROR_SNIPPET_PREFIX_SIZE, 1);

		String poshiScript = rootPoshiElement.getPoshiScript();

		String[] lines = poshiScript.split("\n");

		int endingLineNumber = lines.length;

		endingLineNumber = Math.min(
			errorLineNumber + _ERROR_SNIPPET_POSTFIX_SIZE, endingLineNumber);

		StringBuilder sb = new StringBuilder();

		int currentLineNumber = startingLineNumber;

		String lineNumberString = String.valueOf(endingLineNumber);

		int pad = lineNumberString.length() + 2;

		while (currentLineNumber <= endingLineNumber) {
			StringBuilder prefix = new StringBuilder();

			if (currentLineNumber == errorLineNumber) {
				prefix.append(">");
			}
			else {
				prefix.append(" ");
			}

			prefix.append(" ");

			prefix.append(currentLineNumber);

			sb.append(String.format("%" + pad + "s", prefix.toString()));
			sb.append(" |");

			String line = lines[currentLineNumber - 1];

			sb.append(StringUtil.replace(line, "\t", "    "));

			sb.append("\n");

			currentLineNumber++;
		}

		return sb.toString();
	}

	public String getFilePath() {
		return _filePath;
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getMessage());
		sb.append(" at:\n");
		sb.append(getFilePath());
		sb.append(":");
		sb.append(getErrorLineNumber());

		if (getPoshiNode() != null) {
			sb.append("\n");
			sb.append(getErrorSnippet());
		}

		return sb.toString();
	}

	public PoshiNode<?, ?> getPoshiNode() {
		return _poshiNode;
	}

	public String getSimpleMessage() {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getMessage());
		sb.append(" at:\n");
		sb.append(getFilePath());
		sb.append(":");
		sb.append(getErrorLineNumber());

		return sb.toString();
	}

	public boolean isWarning() {
		if (_warning == null) {
			_warning = _isWarning();
		}

		return _warning;
	}

	public void setErrorLineNumber(int errorLineNumber) {
		_errorLineNumber = errorLineNumber;
	}

	public void setErrorSnippet(String errorSnippet) {
		_errorSnippet = errorSnippet;
	}

	public void setFilePath(String filePath) {
		_filePath = filePath;
	}

	public void setPoshiNode(PoshiNode<?, ?> poshiNode) {
		_poshiNode = poshiNode;
	}

	protected static String getFilePath(PoshiNode<?, ?> poshiNode) {
		URL filePathURL = poshiNode.getFilePathURL();

		return filePathURL.getPath();
	}

	private PoshiElementException(String msg) {
		super(msg);
	}

	private boolean _isWarning() {
		String filePath = getFilePath();

		if (filePath.contains("com.liferay.poshi.runner.resources")) {
			String fileExtension = PoshiGetterUtil.getFileExtensionFromFilePath(
				filePath);

			PoshiProperties poshiProperties =
				PoshiProperties.getPoshiProperties();

			for (String validationResourceFileType :
					poshiProperties.validationResourceFileTypes) {

				if (validationResourceFileType.equals(fileExtension)) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	private static final int _ERROR_SNIPPET_POSTFIX_SIZE = 7;

	private static final int _ERROR_SNIPPET_PREFIX_SIZE = 7;

	private int _errorLineNumber;
	private String _errorSnippet = "";
	private String _filePath = "Unknown file";
	private PoshiNode<?, ?> _poshiNode;
	private Boolean _warning;

}
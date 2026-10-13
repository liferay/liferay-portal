/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.SourceFormatterMessage;
import com.liferay.source.formatter.check.configuration.SourceFormatterSuppressions;
import com.liferay.source.formatter.checkstyle.util.CheckstyleLogger;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;

import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.PackageNamesLoader;
import com.puppycrawl.tools.checkstyle.PackageObjectFactory;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.FilterSet;
import com.puppycrawl.tools.checkstyle.api.Violation;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Hugo Huijser
 */
public class Checker extends com.puppycrawl.tools.checkstyle.Checker {

	public Checker(
			Configuration configuration, AuditListener auditListener,
			CheckstyleLogger checkstyleLogger,
			SourceFormatterSuppressions sourceFormatterSuppressions)
		throws CheckstyleException {

		_configuration = configuration;
		_auditListener = auditListener;

		_checkstyleLogger = checkstyleLogger;

		_checkstyleLogger.clearSourceFormatterMessages();

		_filterSet = sourceFormatterSuppressions.getCheckstyleFilterSet();

		Class<?> clazz = getClass();

		_classLoader = clazz.getClassLoader();

		setModuleClassLoader(_classLoader);

		_moduleFactory = new PackageObjectFactory(
			PackageNamesLoader.getPackageNames(_classLoader), _classLoader);

		addFilter(_filterSet);

		addListener(_auditListener);

		configure(_configuration);
	}

	@Override
	public void fireErrors(String fileName, SortedSet<Violation> errors) {
		super.fireErrors(_normalizeFileName(fileName), errors);
	}

	@Override
	public void fireFileFinished(String fileName) {
		super.fireFileFinished(_normalizeFileName(fileName));
	}

	@Override
	public void fireFileStarted(String fileName) {
		super.fireFileStarted(_normalizeFileName(fileName));
	}

	public Set<SourceFormatterMessage> getSourceFormatterMessages() {
		return _checkstyleLogger.getSourceFormatterMessages();
	}

	public void processFileContents(List<String[]> fileContents)
		throws CheckstyleException, IOException {

		_fireAuditStarted();

		for (Configuration childConfiguration : _configuration.getChildren()) {
			Object childModule = _moduleFactory.createModule(
				childConfiguration.getName());

			if (!(childModule instanceof TreeWalker)) {
				continue;
			}

			TreeWalker treeWalker = (TreeWalker)childModule;

			_processFileContents(fileContents, _getChecks(childConfiguration));

			treeWalker.finishProcessing();

			treeWalker.destroy();
		}

		_fireAuditStarted();
	}

	private void _fireAuditStarted() {
		_auditListener.auditStarted(new AuditEvent(this));
	}

	private List<AbstractCheck> _getChecks(Configuration configuration)
		throws CheckstyleException {

		List<AbstractCheck> checks = new ArrayList<>();

		for (Configuration checkConfiguration : configuration.getChildren()) {
			Object checkModule = _moduleFactory.createModule(
				checkConfiguration.getName());

			if (!(checkModule instanceof AbstractCheck)) {
				continue;
			}

			AbstractCheck check = (AbstractCheck)checkModule;

			// Checkstyle 10 leaves the tab width at 0 until TreeWalker passes
			// its own down, and TreeWalker is bypassed here

			check.setTabWidth(CommonUtil.DEFAULT_TAB_WIDTH);

			check.configure(checkConfiguration);

			checks.add(check);
		}

		return checks;
	}

	private boolean _isRegisteredCheck(AbstractCheck check, int tokenType) {
		Set<String> tokenNames = check.getTokenNames();

		if (tokenNames.isEmpty()) {
			return ArrayUtil.contains(check.getDefaultTokens(), tokenType);
		}

		if (!ArrayUtil.contains(check.getAcceptableTokens(), tokenType)) {
			return false;
		}

		for (String tokenName : tokenNames) {
			if (tokenType == TokenUtil.getTokenId(tokenName)) {
				return true;
			}
		}

		return false;
	}

	private String _normalizeFileName(String fileName) {
		Path filePath = Paths.get(fileName);

		filePath = filePath.normalize();

		return StringUtil.replace(
			filePath.toString(), CharPool.BACK_SLASH, CharPool.SLASH);
	}

	private SortedSet<Violation> _processContent(
			String fileName, String content, List<AbstractCheck> checks)
		throws IOException {

		FileText fileText = new FileText(
			new File(fileName), CheckstyleUtil.getLines(content));

		FileContents fileContents = new FileContents(fileText);

		try {
			DetailAST rootDetailAST = JavaParser.parse(fileContents);

			return _walk(rootDetailAST, fileContents, checks);
		}
		catch (CheckstyleException checkstyleException) {
			if (_log.isDebugEnabled()) {
				_log.debug(checkstyleException);
			}

			return new TreeSet<>();
		}
	}

	private void _processFileContents(
			List<String[]> fileContents, List<AbstractCheck> checks)
		throws IOException {

		for (String[] fileContentArray : fileContents) {
			String content = fileContentArray[1];

			String fileName = fileContentArray[0];

			fireFileStarted(fileName);

			fireErrors(fileName, _processContent(fileName, content, checks));

			fireFileFinished(fileName);
		}
	}

	private SortedSet<Violation> _walk(
		DetailAST rootDetailAST, FileContents fileContents,
		List<AbstractCheck> checks) {

		SortedSet<Violation> messages = new TreeSet<>();

		for (AbstractCheck check : checks) {
			check.clearViolations();
			check.setFileContents(fileContents);

			check.beginTree(rootDetailAST);
		}

		DetailAST detailAST = rootDetailAST;

		while (detailAST != null) {
			for (AbstractCheck check : checks) {
				if (!_isRegisteredCheck(check, detailAST.getType())) {
					continue;
				}

				check.visitToken(detailAST);
			}

			DetailAST firstChildDetailAST = detailAST.getFirstChild();

			while ((detailAST != null) && (firstChildDetailAST == null)) {
				for (AbstractCheck check : checks) {
					if (!_isRegisteredCheck(check, detailAST.getType())) {
						continue;
					}

					check.leaveToken(detailAST);
				}

				firstChildDetailAST = detailAST.getNextSibling();

				detailAST = detailAST.getParent();
			}

			detailAST = firstChildDetailAST;
		}

		for (AbstractCheck check : checks) {
			check.finishTree(rootDetailAST);

			messages.addAll(check.getViolations());
		}

		return messages;
	}

	private static final Log _log = LogFactoryUtil.getLog(Checker.class);

	private final AuditListener _auditListener;
	private final CheckstyleLogger _checkstyleLogger;
	private final ClassLoader _classLoader;
	private final Configuration _configuration;
	private final FilterSet _filterSet;
	private final ModuleFactory _moduleFactory;

}
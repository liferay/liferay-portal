/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;

/**
 * @author Kevin Lee
 */
public class GradleBuildFile {

	public GradleBuildFile(String source) {
		_source = source;
	}

	public void deleteGradleDependencies(
		List<GradleDependency> gradleDependencies) {

		List<String> sourceLines = getSourceLines();

		gradleDependencies.sort(
			GradleDependency.COMPARATOR_LAST_LINE_NUMBER_DESC);

		for (GradleDependency gradleDependency : gradleDependencies) {
			int lineNumber = gradleDependency.getLineNumber();
			int lastLineNumber = gradleDependency.getLastLineNumber();

			for (int i = lastLineNumber; i >= lineNumber; i--) {
				sourceLines.remove(i - 1);
			}
		}

		_saveSource(sourceLines);
	}

	public void deleteGradleDependency(String group, String name) {
		deleteGradleDependency(null, group, name);
	}

	public void deleteGradleDependency(
		String configuration, String group, String name) {

		List<String> sourceLines = getSourceLines();

		List<GradleDependency> gradleDependencies = getGradleDependencies();

		ListIterator<GradleDependency> listIterator =
			gradleDependencies.listIterator(gradleDependencies.size());

		while (listIterator.hasPrevious()) {
			GradleDependency gradleDependency = listIterator.previous();

			if ((configuration != null) &&
				!Objects.equals(
					configuration, gradleDependency.getConfiguration())) {

				continue;
			}

			if (!Objects.equals(group, gradleDependency.getGroup()) ||
				!Objects.equals(name, gradleDependency.getName())) {

				continue;
			}

			for (int i = gradleDependency.getLastLineNumber();
				 i >= gradleDependency.getLineNumber(); i--) {

				sourceLines.remove(i - 1);
			}
		}

		_saveSource(sourceLines);
	}

	public List<GradleDependency> getBuildScriptDependencies() {
		GradleBuildFileVisitor gradleBuildFileVisitor = _walkAST();

		return gradleBuildFileVisitor.getBuildScriptDependencies();
	}

	public List<GradleDependency> getBuildScriptDependencies(
		String configuration) {

		GradleBuildFileVisitor gradleBuildFileVisitor = _walkAST();

		return ListUtil.filter(
			gradleBuildFileVisitor.getBuildScriptDependencies(),
			gradleDependency -> Objects.equals(
				configuration, gradleDependency.getConfiguration()));
	}

	public List<GradleDependency> getGradleDependencies() {
		GradleBuildFileVisitor gradleBuildFileVisitor = _walkAST();

		return gradleBuildFileVisitor.getGradleDependencies();
	}

	public List<GradleDependency> getGradleDependencies(String configuration) {
		GradleBuildFileVisitor gradleBuildFileVisitor = _walkAST();

		return ListUtil.filter(
			gradleBuildFileVisitor.getGradleDependencies(),
			gradleDependency -> Objects.equals(
				configuration, gradleDependency.getConfiguration()));
	}

	public String getSource() {
		return _source;
	}

	public List<String> getSourceLines() {
		return ListUtil.fromString(_source, "\n");
	}

	public void insertGradleDependency(GradleDependency gradleDependency) {
		GradleBuildFileVisitor gradleBuildFileVisitor = _walkAST();

		for (GradleDependency currentGradleDependency :
				gradleBuildFileVisitor.getGradleDependencies()) {

			if (currentGradleDependency.equals(gradleDependency)) {
				return;
			}
		}

		List<String> sourceLines = getSourceLines();

		int dependenciesLastLineNumber =
			gradleBuildFileVisitor.getDependenciesLastLineNumber();

		if (dependenciesLastLineNumber == -1) {
			sourceLines.add("");
			sourceLines.add("dependencies {");
			sourceLines.add("\t" + gradleDependency.toString());
			sourceLines.add("}");
		}
		else {
			sourceLines.add(
				dependenciesLastLineNumber - 1,
				"\t" + gradleDependency.toString());
		}

		_saveSource(sourceLines);
	}

	public void insertGradleDependency(
		String configuration, String group, String name) {

		insertGradleDependency(configuration, group, name, null);
	}

	public void insertGradleDependency(
		String configuration, String group, String name, String version) {

		insertGradleDependency(
			new GradleDependency(configuration, group, name, version));
	}

	private void _saveSource(List<String> lines) {
		_source = StringUtil.merge(lines, "\n");
	}

	private GradleBuildFileVisitor _walkAST() {
		GradleBuildFileVisitor gradleBuildFileVisitor =
			new GradleBuildFileVisitor();

		if (Validator.isNull(_source)) {
			return gradleBuildFileVisitor;
		}

		AstBuilder astBuilder = new AstBuilder();

		try {
			for (ASTNode astNode :
					astBuilder.buildFromString(
						CompilePhase.CONVERSION, _source)) {

				if (astNode instanceof ClassNode) {
					continue;
				}

				astNode.visit(gradleBuildFileVisitor);
			}
		}
		catch (CompilationFailedException compilationFailedException) {
			if (_log.isDebugEnabled()) {
				_log.debug(compilationFailedException);
			}
		}

		return gradleBuildFileVisitor;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GradleBuildFile.class);

	private String _source;

}
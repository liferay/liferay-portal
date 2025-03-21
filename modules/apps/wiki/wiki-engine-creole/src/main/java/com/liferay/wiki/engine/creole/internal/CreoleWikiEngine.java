/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.engine.creole.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.engine.BaseWikiEngine;
import com.liferay.wiki.engine.WikiEngine;
import com.liferay.wiki.engine.creole.internal.antlrwiki.translator.XhtmlTranslator;
import com.liferay.wiki.engine.creole.internal.parser.ast.ASTNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.WikiPageNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.link.LinkNode;
import com.liferay.wiki.engine.creole.internal.parser.parser.Creole10Lexer;
import com.liferay.wiki.engine.creole.internal.parser.parser.Creole10Parser;
import com.liferay.wiki.engine.creole.internal.parser.visitor.LinkNodeCollectorVisitor;
import com.liferay.wiki.exception.PageContentException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Pastor
 */
@Component(
	configurationPid = "com.liferay.wiki.configuration.WikiGroupServiceConfiguration",
	service = WikiEngine.class
)
public class CreoleWikiEngine extends BaseWikiEngine {

	@Override
	public String convert(
		WikiPage page, PortletURL viewPageURL, PortletURL editPageURL,
		String attachmentURLPrefix) {

		XhtmlTranslator xhtmlTranslator = new XhtmlTranslator();

		return xhtmlTranslator.translate(
			page, viewPageURL, editPageURL, attachmentURLPrefix,
			_parse(page.getContent()));
	}

	@Override
	public String getEditorName() {
		return _wikiGroupServiceConfiguration.getCreoleEditor();
	}

	@Override
	public String getFormat() {
		return "creole";
	}

	@Override
	public String getHelpURL() {
		return "http://www.wikicreole.org/wiki/Creole1.0";
	}

	@Override
	public Map<String, Boolean> getOutgoingLinks(WikiPage page)
		throws PageContentException {

		Map<String, Boolean> outgoingLinks = new HashMap<>();

		LinkNodeCollectorVisitor linkNodeCollectorVisitor =
			new LinkNodeCollectorVisitor();

		List<ASTNode> astNodes = linkNodeCollectorVisitor.collect(
			_parse(page.getContent()));

		try {
			for (ASTNode astNode : astNodes) {
				LinkNode linkNode = (LinkNode)astNode;

				String title = linkNode.getLink();

				boolean existingLink = false;

				int count = _wikiPageLocalService.getPagesCount(
					page.getNodeId(), title, true);

				if (count > 0) {
					existingLink = true;
				}

				if (existingLink) {
					title = StringUtil.toLowerCase(title);
				}

				outgoingLinks.put(title, existingLink);
			}
		}
		catch (SystemException systemException) {
			throw new PageContentException(systemException);
		}

		return outgoingLinks;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_wikiGroupServiceConfiguration = ConfigurableUtil.createConfigurable(
			WikiGroupServiceConfiguration.class, properties);
	}

	@Override
	protected ServletContext getEditPageServletContext() {
		return _wikiEngineInputEditorServletContext;
	}

	@Override
	protected ServletContext getHelpPageServletContext() {
		return _servletContext;
	}

	private Creole10Parser _build(String creoleCode) {
		ANTLRStringStream antlrStringStream = new ANTLRStringStream(creoleCode);

		Creole10Lexer creole10Lexer = new Creole10Lexer(antlrStringStream);

		CommonTokenStream commonTokenStream = new CommonTokenStream(
			creole10Lexer);

		return new Creole10Parser(commonTokenStream);
	}

	private WikiPageNode _parse(String creoleCode) {
		Creole10Parser creole10Parser = _build(creoleCode);

		try {
			creole10Parser.wikipage();
		}
		catch (RecognitionException recognitionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to parse:\n" + creoleCode, recognitionException);

				for (String error : creole10Parser.getErrors()) {
					_log.debug(error);
				}
			}
		}

		return creole10Parser.getWikiPageNode();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CreoleWikiEngine.class);

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.wiki.engine.creole)"
	)
	private ServletContext _servletContext;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.wiki.engine.input.editor.common)"
	)
	private ServletContext _wikiEngineInputEditorServletContext;

	private WikiGroupServiceConfiguration _wikiGroupServiceConfiguration;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

}
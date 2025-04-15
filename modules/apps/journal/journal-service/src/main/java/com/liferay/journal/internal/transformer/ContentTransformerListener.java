/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.transformer;

import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.templateparser.BaseTransformerListener;
import com.liferay.portal.kernel.templateparser.TransformerListener;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Tina Tian
 */
@Component(
	configurationPid = "com.liferay.journal.configuration.JournalServiceConfiguration",
	property = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
	service = TransformerListener.class
)
public class ContentTransformerListener extends BaseTransformerListener {

	@Override
	public boolean isEnabled() {
		return _journalServiceConfiguration.enableContentTransformerListener();
	}

	@Override
	public String onScript(
		String script, Document document, String languageId,
		Map<String, String> tokens) {

		if (_log.isDebugEnabled()) {
			_log.debug("onScript");
		}

		return _injectEditInPlace(script, document);
	}

	@Override
	public Document onXml(
		Document document, String languageId, Map<String, String> tokens) {

		if (_log.isDebugEnabled()) {
			_log.debug("onXml");
		}

		replace(document, languageId, tokens);

		return document;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_journalServiceConfiguration = ConfigurableUtil.createConfigurable(
			JournalServiceConfiguration.class, properties);
	}

	protected void replace(
		Document document, String languageId, Map<String, String> tokens) {

		try {
			Element rootElement = document.getRootElement();

			long articleGroupId = GetterUtil.getLong(
				tokens.get("article_group_id"));

			replace(rootElement, articleGroupId, languageId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	protected void replace(Element root, long articleGroupId, String languageId)
		throws Exception {

		for (Element element : root.elements()) {
			List<Element> dynamicContentElements = element.elements(
				"dynamic-content");

			for (Element dynamicContentElement : dynamicContentElements) {
				String text = dynamicContentElement.getText();

				text = HtmlUtil.stripComments(text);
				text = HtmlUtil.stripHtml(text);
				text = text.trim();

				// [@articleId;elementName@]

				if (Validator.isNotNull(text) && (text.length() >= 7) &&
					text.startsWith("[@") && text.endsWith("@]")) {

					text = text.substring(2, text.length() - 2);

					int pos = text.indexOf(";");

					if (pos != -1) {
						String articleId = text.substring(0, pos);
						String elementName = text.substring(pos + 1);

						dynamicContentElement.clearContent();
						dynamicContentElement.addCDATA(
							_getContent(
								JournalArticleLocalServiceUtil.getArticle(
									articleGroupId, articleId),
								elementName, languageId));
					}
				}
				else if ((text != null) &&
						 text.startsWith("/image/journal/article?img_id")) {

					// Make sure to point images to the full path

					dynamicContentElement.setText(
						"@cdn_host@@root_path@" + text);
				}
			}

			replace(element, articleGroupId, languageId);
		}
	}

	/**
	 * Fill one article with content from another approved article. See the
	 * article DOCUMENTATION-INSTALLATION-BORLAND for a sample use case.
	 *
	 * @return the processed string
	 */
	protected String replace(
		String xml, String languageId, Map<String, String> tokens) {

		try {
			Document document = SAXReaderUtil.read(xml);

			replace(document, languageId, tokens);

			document.formattedString(StringPool.DOUBLE_SPACE);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return xml;
	}

	private String _getContent(
		JournalArticle article, String elementName, String languageId) {

		DDMFormValues ddmFormValues = article.getDDMFormValues();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			elementName);

		if (ddmFormFieldValues.isEmpty()) {
			return null;
		}

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		return value.getString(_language.getLocale(languageId));
	}

	private String _injectEditInPlace(String script, Document document) {
		if (!script.contains("$editInPlace(")) {
			return script;
		}

		try {
			List<Node> nodes = document.selectNodes("//dynamic-element");

			for (Node node : nodes) {
				Element element = (Element)node;

				String name = GetterUtil.getString(
					element.attributeValue("name"));
				String type = GetterUtil.getString(
					element.attributeValue("type"));

				if (!name.startsWith("reserved-") &&
					(type.equals("text") || type.equals("text_area") ||
					 type.equals("text_box"))) {

					script = _wrapEditInPlaceField(script, name, type, "data");
					script = _wrapEditInPlaceField(
						script, name, type, "getData()");
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return script;
	}

	private String _wrapEditInPlaceField(
		String script, String name, String type, String call) {

		String field = StringBundler.concat("$", name, ".", call);

		String wrappedField = StringBundler.concat(
			"<span class=\"journal-content-eip-", type, "\" ",
			"id=\"journal-content-field-name-", name, "\">", field, "</span>");

		return StringUtil.replace(
			script, "$editInPlace(" + field + ")", wrappedField);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentTransformerListener.class);

	private volatile JournalServiceConfiguration _journalServiceConfiguration;

	@Reference
	private Language _language;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ImportPackage;
import com.liferay.source.formatter.check.comparator.ElementComparator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class XMLSpringFileCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (fileName.endsWith("-spring.xml")) {
			_checkSpringXML(fileName, content);
		}

		return content;
	}

	private void _checkSpringXML(String fileName, String content) {
		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return;
		}

		Element rootElement = document.getRootElement();

		for (Element beanElement :
				(List<Element>)rootElement.elements("bean")) {

			String name = beanElement.attributeValue("id");

			if (name == null) {
				name = beanElement.attributeValue("class");
			}

			checkElementOrder(
				fileName, beanElement, "property", name,
				new ElementComparator());
		}

		checkElementOrder(
			fileName, rootElement, "bean", null,
			new SpringBeanElementComparator("id"));
	}

	private class SpringBeanElementComparator extends ElementComparator {

		public SpringBeanElementComparator(String nameAttribute) {
			super(nameAttribute);
		}

		@Override
		public int compare(Element element1, Element element2) {
			String elementName1 = getElementName(element1);
			String elementName2 = getElementName(element2);

			if ((elementName1 == null) || (elementName2 == null)) {
				return 0;
			}

			int startsWithWeight = StringUtil.startsWithWeight(
				elementName1, elementName2);

			if (startsWithWeight != 0) {
				String startKey = elementName1.substring(0, startsWithWeight);

				if (startKey.contains(".service.")) {
					return _compareServiceElements(elementName1, elementName2);
				}
			}

			if ((StringUtil.count(elementName1, StringPool.PERIOD) > 1) &&
				(StringUtil.count(elementName2, StringPool.PERIOD) > 1)) {

				ImportPackage importPackage1 = new ImportPackage(
					elementName1, false, elementName1);
				ImportPackage importPackage2 = new ImportPackage(
					elementName2, false, elementName2);

				return importPackage1.compareTo(importPackage2);
			}

			if (StringUtil.count(elementName1, StringPool.PERIOD) > 1) {
				return -1;
			}

			return super.compare(element1, element2);
		}

		@Override
		public String getElementName(Element element) {
			String elementName = super.getElementName(element);

			if ((elementName != null) &&
				(StringUtil.count(elementName, StringPool.PERIOD) > 1)) {

				return elementName;
			}

			return element.attributeValue("class");
		}

		private int _compareServiceElements(String name1, String name2) {
			SpringBeanServiceElement springBeanServiceElement1 =
				new SpringBeanServiceElement(name1);
			SpringBeanServiceElement springBeanServiceElement2 =
				new SpringBeanServiceElement(name2);

			return springBeanServiceElement1.compareTo(
				springBeanServiceElement2);
		}

		private class SpringBeanServiceElement
			implements Comparable<SpringBeanServiceElement> {

			public SpringBeanServiceElement(String name) {
				_beanObjectName = StringPool.BLANK;
				_type = -1;

				Matcher matcher = _ctServicePattern.matcher(name);

				if (matcher.find()) {
					_beanObjectName = matcher.group(1);
					_type = _CT_SERVICE;

					return;
				}

				matcher = _localServicePattern.matcher(name);

				if (matcher.find()) {
					_beanObjectName = matcher.group(1);
					_type = _LOCAL_SERVICE;

					return;
				}

				matcher = _servicePattern.matcher(name);

				if (matcher.find()) {
					_beanObjectName = matcher.group(1);
					_type = _SERVICE;

					return;
				}

				matcher = _persistencePattern.matcher(name);

				if (matcher.find()) {
					_beanObjectName = matcher.group(1);
					_type = _PERSISTENCE;

					return;
				}

				matcher = _finderPattern.matcher(name);

				if (matcher.find()) {
					_beanObjectName = matcher.group(1);
					_type = _FINDER;

					return;
				}

				matcher = _modelArgumentsResolverPattern.matcher(name);

				if (matcher.find()) {
					_beanObjectName = matcher.group(1);
					_type = _MODEL_ARGUMENTS_RESOLVER;
				}
			}

			@Override
			public int compareTo(
				SpringBeanServiceElement springBeanServiceElement) {

				if (_beanObjectName.equals(
						springBeanServiceElement.getBeanObjectName())) {

					return _type - springBeanServiceElement.getType();
				}

				NaturalOrderStringComparator comparator =
					new NaturalOrderStringComparator();

				return comparator.compare(
					_beanObjectName,
					springBeanServiceElement.getBeanObjectName());
			}

			public String getBeanObjectName() {
				return _beanObjectName;
			}

			public int getType() {
				return _type;
			}

			private static final int _CT_SERVICE = 1;

			private static final int _FINDER = 6;

			private static final int _LOCAL_SERVICE = 2;

			private static final int _MODEL_ARGUMENTS_RESOLVER = 4;

			private static final int _PERSISTENCE = 5;

			private static final int _SERVICE = 3;

			private String _beanObjectName;
			private final Pattern _ctServicePattern = Pattern.compile(
				"\\.service\\.(\\w+)CTService");
			private final Pattern _finderPattern = Pattern.compile(
				"\\.service\\.persistence\\.(\\w+)Finder");
			private final Pattern _localServicePattern = Pattern.compile(
				"\\.service\\.(\\w+)LocalService");
			private final Pattern _modelArgumentsResolverPattern =
				Pattern.compile(
					"\\.service\\.persistence\\.impl\\.(\\w+)" +
						"ModelArgumentsResolver");
			private final Pattern _persistencePattern = Pattern.compile(
				"\\.service\\.persistence\\.(\\w+)Persistence");
			private final Pattern _servicePattern = Pattern.compile(
				"\\.service\\.(\\w+)Service");
			private int _type;

		}

	}

}
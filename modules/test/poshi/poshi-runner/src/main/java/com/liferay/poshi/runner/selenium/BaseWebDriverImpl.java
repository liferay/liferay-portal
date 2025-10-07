/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.selenium;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.selenium.AxeReporter;

import com.liferay.poshi.core.PoshiGetterUtil;
import com.liferay.poshi.core.PoshiProperties;
import com.liferay.poshi.core.selenium.LiferaySelenium;
import com.liferay.poshi.core.util.CharPool;
import com.liferay.poshi.core.util.FileUtil;
import com.liferay.poshi.core.util.GetterUtil;
import com.liferay.poshi.core.util.OSDetector;
import com.liferay.poshi.core.util.StringPool;
import com.liferay.poshi.core.util.StringUtil;
import com.liferay.poshi.core.util.Validator;
import com.liferay.poshi.runner.exception.ElementNotFoundPoshiRunnerException;
import com.liferay.poshi.runner.exception.PoshiRunnerWarningException;
import com.liferay.poshi.runner.util.AntCommands;
import com.liferay.poshi.runner.util.ArchiveUtil;
import com.liferay.poshi.runner.util.EmailCommands;
import com.liferay.poshi.runner.util.HtmlUtil;
import com.liferay.poshi.runner.var.type.DefaultTable;
import com.liferay.poshi.runner.var.type.Table;

import com.testautomationguru.ocular.Ocular;
import com.testautomationguru.ocular.OcularConfiguration;
import com.testautomationguru.ocular.comparator.OcularResult;
import com.testautomationguru.ocular.sample.SampleBuilder;
import com.testautomationguru.ocular.snapshot.SnapshotBuilder;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.StringReader;

import java.nio.file.Paths;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.Location;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.robot.Key;
import org.sikuli.api.robot.Keyboard;
import org.sikuli.api.robot.Mouse;
import org.sikuli.api.robot.desktop.DesktopKeyboard;
import org.sikuli.api.robot.desktop.DesktopMouse;
import org.sikuli.api.visual.Canvas;
import org.sikuli.api.visual.CanvasBuilder;
import org.sikuli.api.visual.DesktopCanvas;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class BaseWebDriverImpl implements LiferaySelenium, WebDriver {

	public BaseWebDriverImpl(String browserURL, WebDriver webDriver) {
		System.setProperty("java.awt.headless", "false");

		poshiProperties = PoshiProperties.getPoshiProperties();

		_webDriver = webDriver;

		setDefaultWindowHandle(webDriver.getWindowHandle());

		WebDriver.Options options = webDriver.manage();

		options.deleteAllCookies();

		String browserResolution = poshiProperties.browserResolution;

		if (Validator.isNotNull(browserResolution)) {
			WebDriver.Window window = options.window();

			if (browserResolution.equals("maximize")) {
				window.maximize();
			}
			else {
				window.setSize(_getDimension(browserResolution));
			}
		}

		try {
			webDriver.get(browserURL);
		}
		catch (WebDriverException webDriverException) {
			String message = webDriverException.getMessage();

			if (!message.contains("ERR_CONNECTION_REFUSED")) {
				throw webDriverException;
			}

			System.out.println(browserURL + " is unavailable");
		}

		ocularConfig();
	}

	@Override
	public void acceptAlert() {
		Alert alert = getAlert();

		alert.accept();

		setAlert(null);
	}

	@Override
	public void addSelection(String locator, String optionLocator) {
		Select select = new Select(getWebElement(locator));

		if (optionLocator.startsWith("index=")) {
			select.selectByIndex(
				GetterUtil.getInteger(optionLocator.substring(6)));
		}
		else if (optionLocator.startsWith("label=")) {
			select.selectByVisibleText(optionLocator.substring(6));
		}
		else if (optionLocator.startsWith("value=")) {
			select.selectByValue(optionLocator.substring(6));
		}
		else {
			select.selectByVisibleText(optionLocator);
		}
	}

	@Override
	public void antCommand(String fileName, String target) throws Exception {
		AntCommands antCommands = new AntCommands(fileName, target);

		ExecutorService executorService = Executors.newCachedThreadPool();

		Future<Void> future = executorService.submit(antCommands);

		try {
			future.get(150, TimeUnit.SECONDS);
		}
		catch (ExecutionException executionException) {
			throw executionException;
		}
		catch (TimeoutException timeoutException) {
		}
	}

	@Override
	public void assertAccessible(List<String> ignorableImpacts)
		throws Exception {

		assertElementAccessible(null, ignorableImpacts);
	}

	@Override
	public void assertAlert(String pattern) throws Exception {
		TestCase.assertEquals(pattern, getAlertText());
	}

	@Override
	public void assertAlertNotPresent() throws Exception {
		if (isAlertPresent()) {
			throw new Exception("Alert is present");
		}
	}

	@Override
	public void assertAlertText(String pattern) throws Exception {
		Alert alert = getAlert();

		String alertText = alert.getText();

		if (!pattern.equals(alertText)) {
			String message = StringUtil.combine(
				"Expected text \"", pattern, "\" does not match actual text \"",
				alertText, "\"");

			throw new Exception(message);
		}
	}

	@Override
	public void assertAttributeNotPresent(String locator, String attribute)
		throws Exception {

		if (isAttributePresent(locator, attribute)) {
			throw new Exception(
				"Unexpected attribute \"" + attribute + "\" is present");
		}
	}

	@Override
	public void assertAttributePresent(String locator, String attribute)
		throws Exception {

		if (!isAttributePresent(locator, attribute)) {
			throw new Exception(
				"Expected attribute \"" + attribute + "\" is not present");
		}
	}

	@Override
	public void assertAttributeValue(
			String locator, String attribute, String pattern)
		throws Exception {

		WebElement webElement = getWebElement(locator);

		String actualValue = webElement.getAttribute(attribute);

		if (!pattern.equals(actualValue)) {
			throw new Exception(
				"Actual value of attribute \"" + attribute + "\", \"" +
					actualValue + "\" does not match expected value \"" +
						pattern + "\"");
		}
	}

	@Override
	public void assertChecked(String locator) throws Exception {
		assertElementPresent(locator);

		if (isNotChecked(locator)) {
			throw new Exception(
				"Element is not checked at \"" + locator + "\"");
		}
	}

	@Override
	public void assertConfirmation(String pattern) throws Exception {
		Condition confirmationCondition = getConfirmationCondition(pattern);

		confirmationCondition.assertTrue();
	}

	@Override
	public void assertConsoleErrors() throws Exception {
		LiferaySeleniumUtil.assertConsoleErrors();
	}

	@Override
	public void assertConsoleTextNotPresent(String text) throws Exception {
		Condition consoleTextNotPresentCondition =
			getConsoleTextNotPresentCondition(text);

		consoleTextNotPresentCondition.assertTrue();
	}

	@Override
	public void assertConsoleTextPresent(String text) throws Exception {
		Condition consoleTextPresentCondition = getConsoleTextPresentCondition(
			text);

		consoleTextPresentCondition.assertTrue();
	}

	@Override
	public void assertCssValue(
			String locator, String cssAttribute, String cssValue)
		throws Exception {

		WebElement webElement = getWebElement(locator);

		String actualCssValue = webElement.getCssValue(cssAttribute);

		if (!actualCssValue.equals(cssValue)) {
			throw new Exception(
				"CSS Value " + actualCssValue + " does not match " + cssValue);
		}
	}

	@Override
	public void assertEditable(String locator) throws Exception {
		Condition editableCondition = getEditableCondition(locator);

		editableCondition.assertTrue();
	}

	@Override
	public void assertElementAccessible(
			String locator, List<String> ignorableImpacts)
		throws Exception {

		AxeBuilder axeBuilder = new AxeBuilder();

		axeBuilder.withTags(
			Arrays.asList(
				poshiProperties.accessibilityStandardsTags.split(",")));

		Results results = null;

		if (Validator.isNotNull(locator)) {
			results = axeBuilder.analyze(_webDriver, getWebElement(locator));
		}
		else {
			results = axeBuilder.analyze(_webDriver);
		}

		List<Rule> violations = results.getViolations();

		if (violations.isEmpty()) {
			System.out.println("No accessibility violations were found");

			return;
		}

		List<Rule> rules = new ArrayList<>();

		if (ignorableImpacts == null) {
			rules.addAll(violations);
		}
		else {
			for (Rule violation : violations) {
				if (ignorableImpacts.contains(violation.getImpact())) {
					continue;
				}

				rules.add(violation);
			}
		}

		if (rules.isEmpty()) {
			System.out.println("No accessibility violations were found");

			return;
		}

		AxeReporter.getReadableAxeResults("analyze", this, rules);

		throw new Exception(AxeReporter.getAxeResultString());
	}

	@Override
	public void assertElementFocused(String locator) throws Exception {
		Condition elementFocusedCondition = getElementFocusedCondition(locator);

		elementFocusedCondition.assertTrue();
	}

	@Override
	public void assertElementNotFocused(String locator) throws Exception {
		Condition elementNotFocusedCondition = getElementNotFocusedCondition(
			locator);

		elementNotFocusedCondition.assertTrue();
	}

	@Override
	public void assertElementNotPresent(String locator) throws Exception {
		Condition elementNotPresentCondition = getElementNotPresentCondition(
			locator);

		elementNotPresentCondition.assertTrue();
	}

	@Override
	public void assertElementPresent(String locator) throws Exception {
		Condition elementPresentCondition = getElementPresentCondition(locator);

		elementPresentCondition.assertTrue();
	}

	@Override
	public void assertEmailBody(String index, String body) throws Exception {
		TestCase.assertEquals(body, getEmailBody(index));
	}

	@Override
	public void assertEmailSubject(String index, String subject)
		throws Exception {

		TestCase.assertEquals(subject, getEmailSubject(index));
	}

	@Override
	public void assertHTMLSourceTextNotPresent(String value) throws Exception {
		if (isHTMLSourceTextPresent(value)) {
			throw new Exception(
				"Pattern \"" + value + "\" exists in the HTML source");
		}
	}

	@Override
	public void assertHTMLSourceTextPresent(String value) throws Exception {
		if (!isHTMLSourceTextPresent(value)) {
			throw new Exception(
				"Pattern \"" + value + "\" does not exist in the HTML source");
		}
	}

	@Override
	public void assertJavaScript(
			String javaScript, String message, String argument)
		throws Exception {

		Condition javaScriptCondition = getJavaScriptCondition(
			javaScript, message, argument);

		javaScriptCondition.assertTrue();
	}

	@Override
	public void assertJavaScriptErrors(String ignoreJavaScriptError)
		throws Exception {
	}

	@Override
	public void assertLiferayErrors() throws Exception {
		LiferaySeleniumUtil.assertConsoleErrors();
	}

	@Override
	public void assertLocation(String pattern) throws Exception {
		TestCase.assertEquals(pattern, getLocation());
	}

	@Override
	public void assertNoJavaScriptExceptions() throws Exception {
		LiferaySeleniumUtil.assertNoJavaScriptExceptions();
	}

	@Override
	public void assertNoLiferayExceptions() throws Exception {
		LiferaySeleniumUtil.assertNoLiferayExceptions();
	}

	@Override
	public void assertNotAlert(String pattern) {
		TestCase.assertTrue(Objects.equals(pattern, getAlertText()));
	}

	@Override
	public void assertNotAttributeValue(
			String locator, String attribute, String forbiddenValue)
		throws Exception {

		WebElement webElement = getWebElement(locator);

		String actualValue = webElement.getAttribute(attribute);

		if (forbiddenValue.equals(actualValue)) {
			throw new Exception(
				"Actual value of attribute \"" + attribute +
					"\" matches forbidden value \"" + forbiddenValue + "\"");
		}
	}

	@Override
	public void assertNotChecked(String locator) throws Exception {
		assertElementPresent(locator);

		if (isChecked(locator)) {
			throw new Exception("Element is checked at \"" + locator + "\"");
		}
	}

	@Override
	public void assertNotEditable(String locator) throws Exception {
		Condition notEditable = getNotEditableCondition(locator);

		notEditable.assertTrue();
	}

	@Override
	public void assertNotLocation(String pattern) throws Exception {
		TestCase.assertTrue(Objects.equals(pattern, getLocation()));
	}

	@Override
	public void assertNotPartialText(String locator, String pattern)
		throws Exception {

		assertElementPresent(locator);

		Condition notPartialText = getNotPartialTextCondition(locator, pattern);

		notPartialText.assertTrue();
	}

	@Override
	public void assertNotSelectedLabel(String selectLocator, String pattern)
		throws Exception {

		assertElementPresent(selectLocator);

		Condition notSelectedLabelCondition = getNotSelectedLabelCondition(
			selectLocator, pattern);

		notSelectedLabelCondition.assertTrue();
	}

	@Override
	public void assertNotText(String locator, String pattern) throws Exception {
		assertElementPresent(locator);

		Condition notTextCondition = getNotTextCondition(locator, pattern);

		notTextCondition.assertTrue();
	}

	@Override
	public void assertNotValue(String locator, String pattern)
		throws Exception {

		assertElementPresent(locator);

		Condition notValueCondition = getNotValueCondition(locator, pattern);

		notValueCondition.assertTrue();
	}

	@Override
	public void assertNotVisible(String locator) throws Exception {
		assertElementPresent(locator);

		Condition notVisibleCondition = getNotVisibleCondition(locator);

		notVisibleCondition.assertTrue();
	}

	@Override
	public void assertNotVisibleInPage(String locator) throws Exception {
		assertElementPresent(locator);

		Condition notVisibleInPageCondition = getNotVisibleInPageCondition(
			locator);

		notVisibleInPageCondition.assertTrue();
	}

	@Override
	public void assertNotVisibleInViewport(String locator) throws Exception {
		assertElementPresent(locator);

		Condition notVisibleInViewportCondition =
			getNotVisibleInViewportCondition(locator);

		notVisibleInViewportCondition.assertTrue();
	}

	@Override
	public void assertPartialConfirmation(String pattern) throws Exception {
		String confirmation = getConfirmation(null);

		if (!confirmation.contains(pattern)) {
			throw new Exception(
				"\"" + confirmation + "\" does not contain \"" + pattern +
					"\"");
		}
	}

	@Override
	public void assertPartialLocation(String pattern) throws Exception {
		String location = getLocation();

		if (!location.contains(pattern)) {
			throw new Exception(
				"\"" + location + "\" does not contain \"" + pattern + "\"");
		}
	}

	@Override
	public void assertPartialText(String locator, String pattern)
		throws Exception {

		assertElementPresent(locator);

		Condition partialTextCondition = getPartialTextCondition(
			locator, pattern);

		partialTextCondition.assertTrue();
	}

	@Override
	public void assertPartialTextAceEditor(String locator, String pattern)
		throws Exception {

		assertElementPresent(locator);

		Condition partialTextAceEditorCondition =
			getPartialTextAceEditorCondition(locator, pattern);

		partialTextAceEditorCondition.assertTrue();
	}

	@Override
	public void assertPartialTextCaseInsensitive(String locator, String pattern)
		throws Exception {

		assertElementPresent(locator);

		Condition partialTextCaseInsensitiveCondition =
			getPartialTextCaseInsensitiveCondition(locator, pattern);

		partialTextCaseInsensitiveCondition.assertTrue();
	}

	@Override
	public void assertPrompt(String pattern, String value) throws Exception {
		String confirmation = getConfirmation(value);

		if (!pattern.equals(confirmation)) {
			throw new Exception(
				"Expected text \"" + pattern +
					"\" does not match actual text \"" + confirmation + "\"");
		}
	}

	@Override
	public void assertSelectedLabel(String selectLocator, String pattern)
		throws Exception {

		assertElementPresent(selectLocator);

		Condition selectedLabelCondition = getSelectedLabelCondition(
			selectLocator, pattern);

		selectedLabelCondition.assertTrue();
	}

	@Override
	public void assertTable(String locator, String tableString)
		throws Exception {

		Table htmlTable = getHTMLTable(locator);

		Table table = new DefaultTable(tableString);

		if (htmlTable.getTableSize() != table.getTableSize()) {
			throw new Exception(
				"Expected " + table.getTableSize() + " rows but found " +
					htmlTable.getTableSize() + " rows");
		}

		for (int i = 0; i < htmlTable.getTableSize(); i++) {
			List<String> htmlCellValues = htmlTable.getRowByIndex(i);

			List<String> cellValues = table.getRowByIndex(i);

			if (htmlCellValues.size() != cellValues.size()) {
				throw new Exception(
					"Expected " + cellValues.size() + " columns but found " +
						htmlCellValues.size() + " columns");
			}

			for (int j = 0; j < htmlCellValues.size(); j++) {
				String htmlCellValue = htmlCellValues.get(j);

				String cellValue = cellValues.get(j);

				if (!htmlCellValue.equals(cellValue)) {
					throw new Exception(
						"Expected text \"" + cellValue +
							"\" does not match actual text \"" + htmlCellValue +
								"\"");
				}
			}
		}
	}

	@Override
	public void assertText(String locator, String pattern) throws Exception {
		assertElementPresent(locator);

		Condition textCondition = getTextCondition(locator, pattern);

		textCondition.assertTrue();
	}

	@Override
	public void assertTextCaseInsensitive(String locator, String pattern)
		throws Exception {

		Condition textCaseInsensitiveCondition =
			getTextCaseInsensitiveCondition(locator, pattern);

		textCaseInsensitiveCondition.assertTrue();
	}

	@Override
	public void assertTextMatches(String locator, String regex)
		throws Exception {

		assertElementPresent(locator);

		Condition textMatchedCondition = getTextMatchesCondition(
			locator, regex);

		textMatchedCondition.assertTrue();
	}

	@Override
	public void assertTextNotPresent(String pattern) throws Exception {
		Condition textNotPresentCondition = getTextNotPresentCondition(pattern);

		textNotPresentCondition.assertTrue();
	}

	@Override
	public void assertTextPresent(String pattern) throws Exception {
		Condition textPresentCondition = getTextPresentCondition(pattern);

		textPresentCondition.assertTrue();
	}

	@Override
	public void assertValue(String locator, String pattern) throws Exception {
		assertElementPresent(locator);

		Condition valueCondition = getValueCondition(locator, pattern);

		valueCondition.assertTrue();
	}

	@Override
	public void assertValueMatches(String locator, String regex)
		throws Exception {

		assertElementPresent(locator);

		Condition valueMatchCondition = getValueMatchCondition(locator, regex);

		valueMatchCondition.assertTrue();
	}

	@Override
	public void assertVisible(String locator) throws Exception {
		assertElementPresent(locator);

		Condition visibleCondition = getVisibleCondition(locator);

		visibleCondition.assertTrue();
	}

	@Override
	public void assertVisibleInPage(String locator) throws Exception {
		assertElementPresent(locator);

		Condition visibleInPageCondition = getVisibleInPageCondition(locator);

		visibleInPageCondition.assertTrue();
	}

	@Override
	public void assertVisibleInViewport(String locator) throws Exception {
		assertElementPresent(locator);

		Condition visibleInViewportCondition = getVisibleInViewportCondition(
			locator);

		visibleInViewportCondition.assertTrue();
	}

	@Override
	public void check(String locator) {
		WebElement webElement = getWebElement(locator);

		if (!webElement.isSelected()) {
			webElement.click();
		}
	}

	@Override
	public void click(String locator) {
		if (locator.contains("x:")) {
			String url = getHtmlNodeHref(locator);

			open(url);
		}
		else {
			clickAt(locator, null);
		}
	}

	@Override
	public void clickAt(String locator, String offset) {
		WebElement webElement = getWebElement(locator);

		if (Validator.isNotNull(offset) && offset.contains(",")) {
			scrollWebElementIntoView(webElement);

			Actions actions = new Actions(getWrappedWebDriver(webElement));

			moveToElement(actions, webElement, offset);

			actions.pause(1500);

			actions.click();

			Action action = actions.build();

			action.perform();
		}
		else {
			try {
				webElement.click();
			}
			catch (Exception exception) {
				scrollWebElementIntoView(webElement);

				webElement.click();
			}
		}
	}

	@Override
	public void close() {
		_webDriver.close();
	}

	@Override
	public void connectToEmailAccount(String emailAddress, String emailPassword)
		throws Exception {

		LiferaySeleniumUtil.connectToEmailAccount(emailAddress, emailPassword);
	}

	@Override
	public void copyText(String locator) throws Exception {
		_clipBoard = getText(locator);
	}

	@Override
	public void copyValue(String locator) throws Exception {
		_clipBoard = getElementValue(locator);
	}

	@Override
	public void deleteAllEmails() throws Exception {
		LiferaySeleniumUtil.deleteAllEmails();
	}

	@Override
	public void dismissAlert() {
		Alert alert = getAlert();

		alert.dismiss();

		setAlert(null);
	}

	@Override
	public void doubleClick(String locator) {
		doubleClickAt(locator, null);
	}

	@Override
	public void doubleClickAt(String locator, String offset) {
		WebElement webElement = getWebElement(locator);

		Actions actions = new Actions(getWrappedWebDriver(webElement));

		if (Validator.isNotNull(offset) && offset.contains(",")) {
			moveToElement(actions, webElement, offset);

			actions.doubleClick();
		}
		else {
			actions.doubleClick(webElement);
		}

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void dragAndDrop(String locator, String coordinatePairs) {
		dragAtAndDrop(locator, null, coordinatePairs);
	}

	@Override
	public void dragAndDropToObject(
		String locatorOfObjectToBeDragged,
		String locatorOfDragDestinationObject) {

		WebElement objectToBeDraggedWebElement = getWebElement(
			locatorOfObjectToBeDragged);

		WebDriver wrappedWebDriver = getWrappedWebDriver(
			objectToBeDraggedWebElement);

		Actions actions = new Actions(wrappedWebDriver);

		WebElement dragDestinationObjectWebElement = getWebElement(
			locatorOfDragDestinationObject);

		actions.dragAndDrop(
			objectToBeDraggedWebElement, dragDestinationObjectWebElement);

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void dragAtAndDrop(
		String locator, String offset, String destinationOffsets) {

		Matcher matcher = _coordinatePairsPattern.matcher(destinationOffsets);

		if (!matcher.matches()) {
			throw new IllegalArgumentException(
				"Coordinate pairs \"" + destinationOffsets +
					"\" do not match pattern \"" +
						_coordinatePairsPattern.pattern() + "\"");
		}

		WebElement webElement = getWebElement(locator);

		Actions actions = new Actions(getWrappedWebDriver(webElement));

		if (Validator.isNotNull(offset)) {
			moveToElement(actions, webElement, offset);

			actions.clickAndHold();
		}
		else {
			actions.clickAndHold(webElement);
		}

		actions.pause(1500);

		for (String destinationOffset : destinationOffsets.split("\\|")) {
			String[] destinationOffsetCoordinates = destinationOffset.split(
				",");

			actions.moveByOffset(
				GetterUtil.getInteger(destinationOffsetCoordinates[0]),
				GetterUtil.getInteger(destinationOffsetCoordinates[1]));
		}

		actions.pause(1500);

		actions.release();

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void echo(String message) {
		LiferaySeleniumUtil.echo(message);
	}

	@Override
	public void executeCDPCommand(
		String commandName, Map<String, Object> commandParameters) {

		Augmenter augmenter = new Augmenter();

		WebDriver webDriver = augmenter.augment(getWebDriver());

		HasCdp hasCdp = (HasCdp)webDriver;

		hasCdp.executeCdpCommand(commandName, commandParameters);
	}

	@Override
	public void executeJavaScript(
		String javaScript, String argument1, String argument2) {

		getJavaScriptResult(javaScript, argument1, argument2);
	}

	@Override
	public void fail(String message) {
		LiferaySeleniumUtil.fail(message);
	}

	@Override
	public WebElement findElement(By by) {
		return _webDriver.findElement(by);
	}

	@Override
	public List<WebElement> findElements(By by) {
		return _webDriver.findElements(by);
	}

	@Override
	public void get(String url) {
		try {
			_webDriver.get(url);
		}
		catch (Throwable throwable) {
			throw new WebDriverException(
				"Invalid URL: " + url, throwable.getCause());
		}
	}

	@Override
	public String getAttribute(String attributeLocator) {
		int pos = attributeLocator.lastIndexOf(CharPool.AT);

		String locator = attributeLocator.substring(0, pos);

		WebElement webElement = getWebElement(locator);

		String attribute = attributeLocator.substring(pos + 1);

		return webElement.getAttribute(attribute);
	}

	@Override
	public String getBodyText() {
		WebElement webElement = getWebElement("//body");

		return webElement.getText();
	}

	@Override
	public String getConfirmation(String value) {
		switchTo();

		WebDriverWait webDriverWait = new WebDriverWait(
			this, Duration.ofSeconds(1));

		try {
			Alert alert = webDriverWait.until(
				ExpectedConditions.alertIsPresent());

			String confirmation = alert.getText();

			if (Validator.isNotNull(value)) {
				alert.sendKeys(value);
			}

			alert.accept();

			return confirmation;
		}
		catch (Exception exception) {
			throw new WebDriverException(exception);
		}
	}

	@Override
	public String getCurrentUrl() {
		return _webDriver.getCurrentUrl();
	}

	@Override
	public long getElementHeight(String locator) {
		WebElement webElement = getWebElement(locator);

		Dimension dimension = webElement.getSize();

		return GetterUtil.getLong(dimension.getHeight());
	}

	@Override
	public String getElementValue(String locator) throws Exception {
		return getElementValue(locator, null);
	}

	public String getElementValue(String locator, String timeout) {
		WebElement webElement = getWebElement(locator, timeout);

		scrollWebElementIntoView(webElement);

		return webElement.getAttribute("value");
	}

	@Override
	public long getElementWidth(String locator) {
		WebElement webElement = getWebElement(locator);

		Dimension dimension = webElement.getSize();

		return GetterUtil.getLong(dimension.getWidth());
	}

	@Override
	public String getEmailBody(String index) throws Exception {
		return LiferaySeleniumUtil.getEmailBody(index);
	}

	@Override
	public String getEmailSubject(String index) throws Exception {
		return LiferaySeleniumUtil.getEmailSubject(index);
	}

	@Override
	public String getEval(String script) {
		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver("//body");

		return (String)javascriptExecutor.executeScript(script);
	}

	@Override
	public String getFirstNumber(String locator) {
		WebElement webElement = getWebElement(locator);

		String text = webElement.getText();

		if (text == null) {
			return StringPool.BLANK;
		}

		StringBuilder sb = new StringBuilder();

		char[] chars = text.toCharArray();

		for (char c : chars) {
			boolean digit = false;

			if (Validator.isDigit(c)) {
				sb.append(c);

				digit = true;
			}

			String s = sb.toString();

			if (Validator.isNotNull(s) && !digit) {
				return s;
			}
		}

		return sb.toString();
	}

	@Override
	public String getFirstNumberIncrement(String locator) {
		return String.valueOf(
			GetterUtil.getInteger(getFirstNumber(locator)) + 1);
	}

	public Node getHtmlNode(String locator) {
		try {
			XPathFactory xPathFactory = XPathFactory.newInstance();

			XPath xPath = xPathFactory.newXPath();

			locator = StringUtil.replace(locator, "x:", "");

			XPathExpression xPathExpression = xPath.compile(locator);

			DocumentBuilderFactory documentBuilderFactory =
				DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder =
				documentBuilderFactory.newDocumentBuilder();

			String htmlSource = getHtmlSource();

			htmlSource = htmlSource.substring(htmlSource.indexOf("<html"));

			StringReader stringReader = new StringReader(htmlSource);

			InputSource inputSource = new InputSource(stringReader);

			Document document = documentBuilder.parse(inputSource);

			NodeList nodeList = (NodeList)xPathExpression.evaluate(
				document, XPathConstants.NODESET);

			if (nodeList.getLength() < 1) {
				throw new Exception(locator + " is not present");
			}

			return nodeList.item(0);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	public String getHtmlNodeHref(String locator) {
		Node elementNode = getHtmlNode(locator);

		NamedNodeMap namedNodeMap = elementNode.getAttributes();

		Node attributeNode = namedNodeMap.getNamedItem("href");

		return attributeNode.getTextContent();
	}

	@Override
	public String getHtmlNodeText(String locator) throws Exception {
		Node node = getHtmlNode(locator);

		if (node == null) {
			throw new Exception(locator + " is not present");
		}

		return node.getTextContent();
	}

	@Override
	public String getHtmlSource() {
		return getPageSource();
	}

	@Override
	public String getJavaScriptResult(
		String javaScript, String argument1, String argument2) {

		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver("//body");

		Object object1 = null;
		Object object2 = null;

		try {
			if (Validator.isNotNull(argument1)) {
				object1 = getWebElement(argument1);
			}
		}
		catch (ElementNotFoundPoshiRunnerException | InvalidSelectorException
					exception) {

			object1 = argument1;
		}

		try {
			if (Validator.isNotNull(argument2)) {
				object2 = getWebElement(argument2);
			}
		}
		catch (ElementNotFoundPoshiRunnerException | InvalidSelectorException
					exception) {

			object2 = argument2;
		}

		return (String)javascriptExecutor.executeScript(
			javaScript, object1, object2);
	}

	@Override
	public String getLocation() throws Exception {
		List<Exception> exceptions = new ArrayList<>();

		LocationCallable callable = new LocationCallable();

		for (int i = 0; i < poshiProperties.getLocationMaxRetries; i++) {
			FutureTask<String> futureTask = new FutureTask<>(
				callable._init(this));

			Thread thread = new Thread(futureTask);

			thread.start();

			try {
				return futureTask.get(
					poshiProperties.getLocationTimeout, TimeUnit.SECONDS);
			}
			catch (CancellationException cancellationException) {
				exceptions.add(cancellationException);
			}
			catch (ExecutionException executionException) {
				exceptions.add(executionException);
			}
			catch (InterruptedException interruptedException) {
				exceptions.add(interruptedException);
			}
			catch (TimeoutException timeoutException) {
				exceptions.add(timeoutException);
			}
			finally {
				thread.interrupt();
			}

			System.out.println("getLocation(WebDriver):");
			System.out.println(toString());

			Set<String> windowHandles = getWindowHandles();

			for (String windowHandle : windowHandles) {
				System.out.println(windowHandle);
			}
		}

		if (!exceptions.isEmpty()) {
			throw new Exception(exceptions.get(0));
		}

		throw new TimeoutException();
	}

	@Override
	public String getNumberDecrement(String value) {
		return LiferaySeleniumUtil.getNumberDecrement(value);
	}

	@Override
	public String getNumberIncrement(String value) {
		return LiferaySeleniumUtil.getNumberIncrement(value);
	}

	@Override
	public String getOcularBaselineImageDirName() {
		return _OCULAR_BASELINE_IMAGE_DIR_NAME;
	}

	@Override
	public String getOcularResultImageDirName() {
		return _OCULAR_RESULT_IMAGE_DIR_NAME;
	}

	@Override
	public String getOutputDirName() {
		return _OUTPUT_DIR_NAME;
	}

	@Override
	public String getPageSource() {
		return _webDriver.getPageSource();
	}

	@Override
	public String getPrimaryTestSuiteName() {
		return _primaryTestSuiteName;
	}

	@Override
	public String getSelectedLabel(String selectLocator) {
		return getSelectedLabel(selectLocator, null);
	}

	public String getSelectedLabel(String selectLocator, String timeout) {
		try {
			WebElement selectLocatorWebElement = getWebElement(
				selectLocator, timeout);

			Select select = new Select(selectLocatorWebElement);

			WebElement firstSelectedOptionWebElement =
				select.getFirstSelectedOption();

			return firstSelectedOptionWebElement.getText();
		}
		catch (Exception exception) {
			return null;
		}
	}

	@Override
	public String[] getSelectedLabels(String selectLocator) {
		WebElement selectLocatorWebElement = getWebElement(selectLocator);

		Select select = new Select(selectLocatorWebElement);

		List<WebElement> allSelectedOptionsWebElements =
			select.getAllSelectedOptions();

		String[] selectedOptionsWebElements =
			new String[allSelectedOptionsWebElements.size()];

		for (int i = 0; i < allSelectedOptionsWebElements.size(); i++) {
			WebElement webElement = allSelectedOptionsWebElements.get(i);

			if (webElement != null) {
				selectedOptionsWebElements[i] = webElement.getText();
			}
		}

		return selectedOptionsWebElements;
	}

	@Override
	public String getSikuliImagesDirName() {
		return _SIKULI_IMAGES_DIR_NAME;
	}

	@Override
	public String getTestDependenciesDirName() {
		return _TEST_DEPENDENCIES_DIR_NAME;
	}

	@Override
	public String getTestName() {
		return _testName;
	}

	@Override
	public String getText(String locator) throws Exception {
		return getText(locator, null);
	}

	public String getText(String locator, String timeout) throws Exception {
		if (locator.contains("x:")) {
			return getHtmlNodeText(locator);
		}

		WebElement webElement = getWebElement(locator, timeout);

		scrollWebElementIntoView(webElement);

		String text = webElement.getText();

		text = text.trim();

		return StringUtil.replace(text, "\n", " ");
	}

	public String getTextAceEditor(String locator) throws Exception {
		return getTextAceEditor(locator, null);
	}

	public String getTextAceEditor(String locator, String timeout) {
		WebElement webElement = getWebElement(locator, timeout);

		scrollWebElementIntoView(webElement);

		String text = webElement.getText();

		text = text.trim();

		return StringUtil.replace(text, "\n", "");
	}

	@Override
	public String getTitle() {
		return _webDriver.getTitle();
	}

	@Override
	public String getWebElementAttribute(String locator, String attributeName) {
		WebElement webElement = getWebElement(locator);

		return webElement.getAttribute(attributeName);
	}

	@Override
	public String getWindowHandle() {
		return _webDriver.getWindowHandle();
	}

	@Override
	public Set<String> getWindowHandles() {
		return _webDriver.getWindowHandles();
	}

	@Override
	public void goBack() {
		WebDriver.Navigation navigation = navigate();

		navigation.back();
	}

	@Override
	public boolean isAlertPresent() {
		boolean alertPresent = false;

		switchTo();

		try {
			WebDriverWait webDriverWait = new WebDriverWait(
				this, Duration.ofSeconds(1));

			webDriverWait.until(ExpectedConditions.alertIsPresent());

			alertPresent = true;
		}
		catch (Exception exception) {
			alertPresent = false;
		}

		return alertPresent;
	}

	@Override
	public boolean isAttributeNotPresent(String locator, String attribute) {
		return !isAttributePresent(locator, attribute);
	}

	@Override
	public boolean isAttributePresent(String locator, String attribute) {
		WebElement webElement = getWebElement(locator);

		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver(webElement);

		StringBuilder sb = new StringBuilder();

		sb.append("var element = arguments[0];");
		sb.append("return element.attributes[\'");
		sb.append(attribute);
		sb.append("\'];");

		Object returnObject = javascriptExecutor.executeScript(
			sb.toString(), webElement);

		if (returnObject != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isChecked(String locator) {
		WebElement webElement = getWebElement(locator);

		scrollWebElementIntoView(webElement);

		return webElement.isSelected();
	}

	@Override
	public boolean isConfirmation(String pattern) throws Exception {
		Condition confirmationCondition = getConfirmationCondition(pattern);

		return confirmationCondition.evaluate();
	}

	@Override
	public boolean isConsoleTextNotPresent(String text) throws Exception {
		Condition consoleTextNotPresentCondition =
			getConsoleTextNotPresentCondition(text);

		return consoleTextNotPresentCondition.evaluate();
	}

	@Override
	public boolean isConsoleTextPresent(String text) throws Exception {
		Condition consoleTextPresentCondition = getConsoleTextPresentCondition(
			text);

		return consoleTextPresentCondition.evaluate();
	}

	@Override
	public boolean isEditable(String locator) throws Exception {
		Condition editableCondition = getEditableCondition(locator);

		return editableCondition.evaluate();
	}

	@Override
	public boolean isElementFocused(String locator) throws Exception {
		Condition elementFocusedCondition = getElementFocusedCondition(locator);

		return elementFocusedCondition.evaluate();
	}

	@Override
	public boolean isElementNotPresent(String locator) throws Exception {
		Condition elementNotPresentCondition = getElementNotPresentCondition(
			locator);

		return elementNotPresentCondition.evaluate();
	}

	@Override
	public boolean isElementPresent(String locator) throws Exception {
		Condition elementPresentCondition = getElementPresentCondition(locator);

		return elementPresentCondition.evaluate();
	}

	@Override
	public boolean isElementPresentAfterWait(String locator) throws Exception {
		for (int second = 0;; second++) {
			if (second >= poshiProperties.timeoutExplicitWait) {
				return isElementPresent(locator);
			}

			if (isElementPresent(locator)) {
				break;
			}

			Thread.sleep(1000);
		}

		return isElementPresent(locator);
	}

	@Override
	public boolean isHTMLSourceTextPresent(String value) throws Exception {
		String pageSource = getPageSource();

		return pageSource.contains(value);
	}

	@Override
	public boolean isNotChecked(String locator) {
		return !isChecked(locator);
	}

	@Override
	public boolean isNotEditable(String locator) throws Exception {
		return !isEditable(locator);
	}

	@Override
	public boolean isNotPartialText(String locator, String value)
		throws Exception {

		return !isPartialText(locator, value);
	}

	@Override
	public boolean isNotPartialTextAceEditor(String locator, String value)
		throws Exception {

		return !isPartialTextAceEditor(locator, value);
	}

	@Override
	public boolean isNotSelectedLabel(String selectLocator, String pattern)
		throws Exception {

		Condition notSelectedLabelCondition = getNotSelectedLabelCondition(
			selectLocator, pattern);

		return notSelectedLabelCondition.evaluate();
	}

	@Override
	public boolean isNotText(String locator, String value) throws Exception {
		return !isText(locator, value);
	}

	@Override
	public boolean isNotValue(String locator, String value) throws Exception {
		return !isValue(locator, value);
	}

	@Override
	public boolean isNotVisible(String locator) throws Exception {
		return !isVisible(locator);
	}

	@Override
	public boolean isNotVisibleInPage(String locator) throws Exception {
		return !isVisibleInPage(locator);
	}

	@Override
	public boolean isNotVisibleInViewport(String locator) throws Exception {
		return !isVisibleInViewport(locator);
	}

	@Override
	public boolean isPartialText(String locator, String value)
		throws Exception {

		Condition partialTextCondition = getPartialTextCondition(
			locator, value);

		return partialTextCondition.evaluate();
	}

	@Override
	public boolean isPartialTextAceEditor(String locator, String value)
		throws Exception {

		Condition partialTextAceEditorCondition =
			getPartialTextAceEditorCondition(locator, value);

		return partialTextAceEditorCondition.evaluate();
	}

	@Override
	public boolean isPartialTextCaseInsensitive(String locator, String value)
		throws Exception {

		Condition partialTextCaseInsensitiveCondition =
			getPartialTextCaseInsensitiveCondition(locator, value);

		return partialTextCaseInsensitiveCondition.evaluate();
	}

	@Override
	public boolean isSelectedLabel(String selectLocator, String pattern)
		throws Exception {

		Condition selectedLabelCondition = getSelectedLabelCondition(
			selectLocator, pattern);

		return selectedLabelCondition.evaluate();
	}

	@Override
	public boolean isSikuliImagePresent(String image) throws Exception {
		ScreenRegion screenRegion = new DesktopScreenRegion();

		if (screenRegion.find(getImageTarget(image)) != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isTCatEnabled() {
		return poshiProperties.tcatEnabled;
	}

	@Override
	public boolean isTestName(String testName) {
		String expectedTestName =
			PoshiGetterUtil.getClassCommandNameFromNamespacedClassCommandName(
				getTestName());

		return testName.equals(expectedTestName);
	}

	@Override
	public boolean isText(String locator, String value) throws Exception {
		Condition textCondition = getTextCondition(locator, value);

		return textCondition.evaluate();
	}

	@Override
	public boolean isTextCaseInsensitive(String locator, String value)
		throws Exception {

		Condition textCaseInsensitiveCondition =
			getTextCaseInsensitiveCondition(locator, value);

		return textCaseInsensitiveCondition.evaluate();
	}

	@Override
	public boolean isTextNotPresent(String pattern) throws Exception {
		return !isTextPresent(pattern);
	}

	@Override
	public boolean isTextPresent(String pattern) throws Exception {
		Condition textPresentCondition = getTextPresentCondition(pattern);

		return textPresentCondition.evaluate();
	}

	@Override
	public boolean isValue(String locator, String value) throws Exception {
		Condition valueConditionCondition = getValueCondition(locator, value);

		return valueConditionCondition.evaluate();
	}

	@Override
	public boolean isVisible(String locator) throws Exception {
		return isVisibleInPage(locator);
	}

	@Override
	public boolean isVisibleInPage(String locator) throws Exception {
		Condition visibleInPageCondition = getVisibleInPageCondition(locator);

		return visibleInPageCondition.evaluate();
	}

	@Override
	public boolean isVisibleInViewport(String locator) throws Exception {
		Condition visibleInViewportCondition = getVisibleInViewportCondition(
			locator);

		return visibleInViewportCondition.evaluate();
	}

	@Override
	public void javaScriptClick(String locator) {
		executeJavaScriptEvent(locator, "MouseEvent", "click");
	}

	@Override
	public void javaScriptDoubleClick(String locator) {
		executeJavaScriptEvent(locator, "MouseEvent", "dblclick");
	}

	public String javaScriptGetText(String locator, String timeout)
		throws Exception {

		if (locator.contains("x:")) {
			return getHtmlNodeText(locator);
		}

		WebElement webElement = getWebElement(locator, timeout);

		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver(webElement);

		StringBuilder sb = new StringBuilder(2);

		sb.append("var element = arguments[0];");
		sb.append("return element.innerText;");

		String text = (String)javascriptExecutor.executeScript(
			sb.toString(), webElement);

		text = text.trim();

		return StringUtil.replace(text, "\n", " ");
	}

	@Override
	public void javaScriptMouseDown(String locator) {
		executeJavaScriptEvent(locator, "MouseEvent", "mousedown");
	}

	@Override
	public void javaScriptMouseOver(String locator) {
		executeJavaScriptEvent(locator, "MouseEvent", "mouseover");
	}

	@Override
	public void javaScriptMouseUp(String locator) {
		executeJavaScriptEvent(locator, "MouseEvent", "mouseup");
	}

	@Override
	public void keyDown(String locator, String keySequence) {
		WebElement webElement = getWebElement(locator);

		Actions actions = new Actions(getWrappedWebDriver(webElement));

		String keycode = keySequence.substring(1);

		Keys keys = Keys.valueOf(keycode);

		actions.keyDown(webElement, keys);

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void keyPress(String locator, String keySequence) {
		WebElement webElement = getWebElement(locator);

		if (keySequence.startsWith("\\")) {
			String keycode = keySequence.substring(1);

			if (isValidKeycode(keycode)) {
				Keys keys = Keys.valueOf(keycode);

				if (keycode.equals("ALT") || keycode.equals("COMMAND") ||
					keycode.equals("CONTROL") || keycode.equals("SHIFT")) {

					Actions actions = new Actions(
						getWrappedWebDriver(webElement));

					actions.keyDown(webElement, keys);
					actions.keyUp(webElement, keys);

					Action action = actions.build();

					action.perform();
				}
				else {
					webElement.sendKeys(keys);
				}
			}
		}
		else {
			webElement.sendKeys(keySequence);
		}
	}

	@Override
	public void keyUp(String locator, String keySequence) {
		WebElement webElement = getWebElement(locator);

		Actions actions = new Actions(getWrappedWebDriver(webElement));

		String keycode = keySequence.substring(1);

		Keys keys = Keys.valueOf(keycode);

		actions.keyUp(webElement, keys);

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void makeVisible(String locator) {
		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver("//body");

		StringBuilder sb = new StringBuilder();

		sb.append("var element = arguments[0];");
		sb.append("element.style.cssText = 'display:inline !important';");
		sb.append("element.style.overflow = 'visible';");
		sb.append("element.style.minHeight = '1px';");
		sb.append("element.style.minWidth = '1px';");
		sb.append("element.style.opacity = '1';");
		sb.append("element.style.visibility = 'visible';");

		WebElement locatorWebElement = getWebElement(locator);

		javascriptExecutor.executeScript(sb.toString(), locatorWebElement);
	}

	@Override
	public Options manage() {
		return _webDriver.manage();
	}

	@Override
	public void maximizeWindow() {
		Options option = _webDriver.manage();

		Window window = option.window();

		window.maximize();
	}

	@Override
	public void mouseDown(String locator) {
		mouseDownAt(locator, null);
	}

	@Override
	public void mouseDownAt(String locator, String offset) {
		WebElement webElement = getWebElement(locator);

		scrollWebElementIntoView(webElement);

		Actions actions = new Actions(getWrappedWebDriver(webElement));

		if (Validator.isNotNull(offset) && offset.contains(",")) {
			moveToElement(actions, webElement, offset);

			actions.clickAndHold();
		}
		else {
			actions.moveToElement(webElement);

			actions.clickAndHold(webElement);
		}

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void mouseMove(String locator) {
		mouseMoveAt(locator, null);
	}

	@Override
	public void mouseMoveAt(String locator, String offset) {
		WebElement webElement = getWebElement(locator);

		scrollWebElementIntoView(webElement);

		Actions actions = new Actions(getWrappedWebDriver(webElement));

		if (Validator.isNotNull(offset) && offset.contains(",")) {
			moveToElement(actions, webElement, offset);
		}
		else {
			actions.moveToElement(webElement);
		}

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void mouseOut(String locator) {
		WebElement webElement = getWebElement(locator);

		scrollWebElementIntoView(webElement);

		Actions actions = new Actions(getWrappedWebDriver(webElement));

		actions.moveToElement(webElement);
		actions.moveByOffset(10, 10);

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void mouseOver(String locator) {
		WebElement webElement = getWebElement(locator);

		scrollWebElementIntoView(webElement);

		Actions actions = new Actions(getWrappedWebDriver(webElement));

		actions.moveToElement(webElement);

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void mouseRelease() {
		Actions actions = new Actions(getWrappedWebDriver("//body"));

		actions.release();

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void mouseUp(String locator) {
		mouseUpAt(locator, null);
	}

	@Override
	public void mouseUpAt(String locator, String offset) {
		WebElement webElement = getWebElement(locator);

		scrollWebElementIntoView(webElement);

		Actions actions = new Actions(getWrappedWebDriver(webElement));

		if (Validator.isNotNull(offset) && offset.contains(",")) {
			moveToElement(actions, webElement, offset);

			actions.release();
		}
		else {
			actions.moveToElement(webElement);
			actions.release(webElement);
		}

		Action action = actions.build();

		action.perform();
	}

	@Override
	public Navigation navigate() {
		return _webDriver.navigate();
	}

	@Override
	public void ocularAssertElementImage(
			String locator, String fileName, String match)
		throws Exception {

		File baselineFile = new File(
			poshiProperties.testBaseDirName + getOcularBaselineImageDirName() +
				"/" + fileName);

		if (!baselineFile.exists()) {
			File snapParentFile = baselineFile.getParentFile();

			snapParentFile.mkdirs();
		}

		File resultFile = new File(
			poshiProperties.testBaseDirName + getOcularResultImageDirName() +
				"/" + fileName);

		File resultParentFile = resultFile.getParentFile();

		resultParentFile.mkdirs();

		OcularConfiguration ocularConfiguration = Ocular.config();

		ocularConfiguration.resultPath(Paths.get(resultParentFile.getPath()));

		ocularConfiguration.globalSimilarity(GetterUtil.getInteger(match));

		WebElement webElement = getWebElement(locator);

		SnapshotBuilder snapshotBuilder = Ocular.snapshot();

		snapshotBuilder = snapshotBuilder.from(Paths.get(fileName));

		SampleBuilder sampleBuilder = snapshotBuilder.sample();

		sampleBuilder = sampleBuilder.using(_webDriver);

		sampleBuilder.element(webElement);

		OcularResult ocularResult = sampleBuilder.compare();

		if (!ocularResult.isEqualsImages()) {
			throw new Exception(
				"Actual element image does not match expected element image");
		}
	}

	@Override
	public void open(String url) {
		String targetURL = url.trim();

		if (targetURL.startsWith("/")) {
			targetURL = poshiProperties.portalURL + targetURL;
		}

		get(targetURL);
	}

	@Override
	public void openWindow(String url, String windowID) {
		open(url);
	}

	@Override
	public void paste(String location) throws Exception {
		type(location, _clipBoard);
	}

	@Override
	public void pause(String durationString) throws Exception {
		int duration = GetterUtil.getInteger(durationString);

		_totalPauseDuration = _totalPauseDuration + duration;

		LiferaySeleniumUtil.pause(duration);
	}

	@Override
	public void pauseLoggerCheck() throws Exception {
	}

	@Override
	public void quit() {
		System.out.println(
			"Total duration of 'LiferaySelenium.pause' usages: " +
				_totalPauseDuration + " ms");

		_webDriver.quit();
	}

	@Override
	public void refresh() {
		String url = getCurrentUrl();

		open(url);

		if (isAlertPresent()) {
			getConfirmation(null);
		}
	}

	@Override
	public void replyToEmail(String to, String body) throws Exception {
		EmailCommands.replyToEmail(to, body);

		pause("3000");
	}

	@Override
	public Map<String, Object> returnCDPCommand(
		String commandName, Map<String, Object> commandParameters) {

		Augmenter augmenter = new Augmenter();

		WebDriver webDriver = augmenter.augment(getWebDriver());

		HasCdp hasCdp = (HasCdp)webDriver;

		return hasCdp.executeCdpCommand(commandName, commandParameters);
	}

	@Override
	public void rightClick(String locator) {
		WebElement webElement = getWebElement(locator);

		Actions actions = new Actions(getWrappedWebDriver(webElement));

		actions.contextClick(webElement);

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void robotType(String value) {
		Keyboard keyboard = new DesktopKeyboard();

		keyboard.type(value);
	}

	@Override
	public void robotTypeShortcut(String value) {
		Keyboard keyboard = new DesktopKeyboard();

		List<String> keys = Arrays.asList(value.split("\\s\\+\\s"));

		Collections.sort(keys, Comparator.comparing(String::length));

		Collections.reverse(keys);

		for (String key : keys) {
			if (key.length() == 1) {
				keyboard.type(key);
			}
			else {
				keyboard.keyDown(_keyCodeMap.get(key.toUpperCase()));
			}
		}

		Collections.reverse(keys);

		for (String key : keys) {
			if (key.length() == 1) {
			}
			else {
				keyboard.keyUp(_keyCodeMap.get(key.toUpperCase()));
			}
		}
	}

	@Override
	public void runScript(String script) {
		getEval(script);
	}

	@Override
	public void saveScreenshot(String fileName) throws Exception {
		if (!poshiProperties.saveScreenshot) {
			return;
		}

		try {
			TakesScreenshot takesScreenshot = (TakesScreenshot)_webDriver;

			FileUtil.write(
				new File(fileName),
				takesScreenshot.getScreenshotAs(OutputType.BYTES));
		}
		catch (NoSuchWindowException noSuchWindowException) {
			selectWindow("null");

			saveScreenshot(fileName);
		}
		catch (UnhandledAlertException unhandledAlertException) {
			System.out.println("Unable to save screenshot due to alert");
		}
	}

	@Override
	public void scrollBy(String offset) {
		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver("//html");

		javascriptExecutor.executeScript("window.scrollBy(" + offset + ");");
	}

	@Override
	public void scrollWebElementIntoView(String locator) throws Exception {
		scrollWebElementIntoView(getWebElement(locator));
	}

	@Override
	public void select(String selectLocator, String optionLocator) {
		Select select = new Select(getWebElement(selectLocator));

		String label = optionLocator;

		if (optionLocator.startsWith("index=")) {
			String indexString = optionLocator.substring(6);

			int index = GetterUtil.getInteger(indexString);

			select.selectByIndex(index - 1);
		}
		else if (optionLocator.startsWith("value=")) {
			String value = optionLocator.substring(6);

			if (value.startsWith("regexp:")) {
				String regexp = value.substring(7);

				selectByRegexpValue(selectLocator, regexp);
			}
			else {
				List<WebElement> optionWebElements = select.getOptions();

				for (WebElement optionWebElement : optionWebElements) {
					String optionWebElementValue =
						optionWebElement.getAttribute("value");

					if (optionWebElementValue.equals(value)) {
						label = optionWebElementValue;

						break;
					}
				}

				select.selectByValue(label);
			}
		}
		else {
			if (optionLocator.startsWith("label=")) {
				label = optionLocator.substring(6);
			}

			if (label.startsWith("regexp:")) {
				String regexp = label.substring(7);

				selectByRegexpText(selectLocator, regexp);
			}
			else {
				select.selectByVisibleText(label);
			}
		}
	}

	@Override
	public void selectFieldText() {
		LiferaySeleniumUtil.selectFieldText();
	}

	@Override
	public void selectFrame(String locator) {
		WebDriver.TargetLocator targetLocator = switchTo();

		if (locator.equals("relative=parent")) {
			if (!_frameWebElements.isEmpty()) {
				_frameWebElements.pop();
			}

			targetLocator.parentFrame();
		}
		else if (locator.equals("relative=top")) {
			_frameWebElements.clear();

			targetLocator.defaultContent();
		}
		else {
			_frameWebElements.push(getWebElement(locator));

			targetLocator.frame(_frameWebElements.peek());
		}
	}

	@Override
	public void selectPopUp(String windowID) {
		if (windowID.equals("") || windowID.equals("null")) {
			String title = getTitle();

			Set<String> windowHandles = getWindowHandles();

			for (String windowHandle : windowHandles) {
				WebDriver.TargetLocator targetLocator = switchTo();

				targetLocator.window(windowHandle);

				if (!title.equals(getTitle())) {
					return;
				}
			}
		}
		else {
			selectWindow(windowID);
		}
	}

	@Override
	public void selectWindow(String windowID) {
		Set<String> windowHandles = getWindowHandles();

		if (windowID.equals("name=undefined")) {
			String title = getTitle();

			for (String windowHandle : windowHandles) {
				WebDriver.TargetLocator targetLocator = switchTo();

				targetLocator.window(windowHandle);

				if (!title.equals(getTitle())) {
					return;
				}
			}

			TestCase.fail("Unable to find the window ID \"" + windowID + "\"");
		}
		else if (windowID.equals("null")) {
			WebDriver.TargetLocator targetLocator = switchTo();

			targetLocator.window(_defaultWindowHandle);
		}
		else {
			String targetWindowTitle = windowID;

			if (targetWindowTitle.startsWith("title=")) {
				targetWindowTitle = targetWindowTitle.substring(6);
			}

			for (String windowHandle : windowHandles) {
				WebDriver.TargetLocator targetLocator = switchTo();

				targetLocator.window(windowHandle);

				if (targetWindowTitle.equals(getTitle())) {
					return;
				}
			}

			TestCase.fail("Unable to find the window ID \"" + windowID + "\"");
		}
	}

	@Override
	public void sendEmail(String to, String subject, String body)
		throws Exception {

		EmailCommands.sendEmail(to, subject, body);

		pause("3000");
	}

	@Override
	public void sendKeys(String locator, String value) throws Exception {
		typeKeys(locator, value);
	}

	@Override
	public void sendKeysAceEditor(String locator, String value)
		throws Exception {

		WebElement webElement = getWebElement(locator);

		webElement.sendKeys(Keys.chord(Keys.CONTROL, Keys.END));

		typeKeys(locator, "");

		Matcher matcher = _aceEditorPattern.matcher(value);

		int x = 0;

		while (matcher.find()) {
			int y = matcher.start();

			String line = value.substring(x, y);

			webElement.sendKeys(line.trim());

			String specialCharacter = matcher.group();

			if (specialCharacter.equals("(")) {
				webElement.sendKeys("(");
			}
			else if (specialCharacter.equals("${line.separator}")) {
				keyPress(locator, "\\SPACE");
				keyPress(locator, "\\RETURN");
			}

			x = y + specialCharacter.length();
		}

		String line = value.substring(x);

		webElement.sendKeys(line.trim());
	}

	@Override
	public void setDefaultTimeout() {
	}

	@Override
	public void setDefaultTimeoutImplicit() {
		int timeout = poshiProperties.timeoutImplicitWait * 1000;

		setTimeoutImplicit(String.valueOf(timeout));
	}

	@Override
	public void setPrimaryTestSuiteName(String primaryTestSuiteName) {
		_primaryTestSuiteName = primaryTestSuiteName;
	}

	@Override
	public void setTestName(String testName) {
		_testName = testName;
	}

	@Override
	public void setTimeout(String timeout) {
	}

	@Override
	public void setTimeoutImplicit(String timeout) {
		WebDriver.Options options = manage();

		if (!poshiProperties.browserType.equals("safari")) {
			WebDriver.Timeouts timeouts = options.timeouts();

			timeouts.implicitlyWait(
				GetterUtil.getInteger(timeout), TimeUnit.SECONDS);
		}
	}

	@Override
	public void setWindowSize(String size) {
		WebDriver wrappedWebDriver = getWrappedWebDriver("//body");

		WebDriver.Options options = wrappedWebDriver.manage();

		WebDriver.Window window = options.window();

		window.setSize(_getDimension(size));
	}

	@Override
	public void sikuliAssertElementNotPresent(String image) throws Exception {
		ScreenRegion screenRegion = new DesktopScreenRegion();

		if (screenRegion.wait(getImageTarget(image), 5000) != null) {
			throw new Exception("Element is present");
		}
	}

	@Override
	public void sikuliAssertElementPresent(String image) throws Exception {
		ScreenRegion screenRegion = new DesktopScreenRegion();

		screenRegion = screenRegion.wait(getImageTarget(image), 5000);

		if (screenRegion == null) {
			throw new Exception("Element is not present");
		}

		Canvas canvas = new DesktopCanvas();

		CanvasBuilder.ElementAdder elementAdder = canvas.add();

		CanvasBuilder.ElementAreaSetter elementAreaSetter = elementAdder.box();

		elementAreaSetter.around(screenRegion);

		canvas.display(2);
	}

	@Override
	public void sikuliClick(String image) throws Exception {
		ScreenRegion screenRegion = new DesktopScreenRegion();

		ScreenRegion imageTargetScreenRegion = screenRegion.find(
			getImageTarget(image));

		if (imageTargetScreenRegion != null) {
			Mouse mouse = new DesktopMouse();

			mouse.click(imageTargetScreenRegion.getCenter());
		}
	}

	@Override
	public void sikuliClickByIndex(String image, String index)
		throws Exception {

		ScreenRegion screenRegion = new DesktopScreenRegion();

		List<ScreenRegion> imageTargetScreenRegions = screenRegion.findAll(
			getImageTarget(image));

		ScreenRegion imageTargetScreenRegion = imageTargetScreenRegions.get(
			GetterUtil.getInteger(index));

		if (imageTargetScreenRegion != null) {
			Mouse mouse = new DesktopMouse();

			mouse.click(imageTargetScreenRegion.getCenter());
		}
	}

	@Override
	public void sikuliDragAndDrop(String image, String offset)
		throws Exception {

		ScreenRegion screenRegion = new DesktopScreenRegion();

		screenRegion = screenRegion.find(getImageTarget(image));

		Mouse mouse = new DesktopMouse();

		mouse.move(screenRegion.getCenter());

		Robot robot = new Robot();

		robot.delay(1000);

		mouse.press();

		robot.delay(2000);

		String[] offsetCoordinates = offset.split(",");

		Location location = screenRegion.getCenter();

		int x = location.getX() + GetterUtil.getInteger(offsetCoordinates[0]);
		int y = location.getY() + GetterUtil.getInteger(offsetCoordinates[1]);

		robot.mouseMove(x, y);

		robot.delay(1000);

		mouse.release();
	}

	@Override
	public void sikuliLeftMouseDown() throws Exception {
		pause("1000");

		Mouse mouse = new DesktopMouse();

		mouse.press();
	}

	@Override
	public void sikuliLeftMouseUp() throws Exception {
		pause("1000");

		Mouse mouse = new DesktopMouse();

		mouse.release();
	}

	@Override
	public void sikuliMouseMove(String image) throws Exception {
		ScreenRegion screenRegion = new DesktopScreenRegion();

		screenRegion = screenRegion.find(getImageTarget(image));

		Mouse mouse = new DesktopMouse();

		mouse.move(screenRegion.getCenter());
	}

	@Override
	public void sikuliRightMouseDown() throws Exception {
		pause("1000");

		Mouse mouse = new DesktopMouse();

		mouse.rightPress();
	}

	@Override
	public void sikuliRightMouseUp() throws Exception {
		pause("1000");

		Mouse mouse = new DesktopMouse();

		mouse.rightRelease();
	}

	@Override
	public void sikuliType(String image, String value) throws Exception {
		sikuliClick(image);

		pause("1000");

		Keyboard keyboard = new DesktopKeyboard();

		if (value.contains("${line.separator}")) {
			String[] tokens = StringUtil.split(value, "${line.separator}");

			for (int i = 0; i < tokens.length; i++) {
				keyboard.type(tokens[i]);

				if ((i + 1) < tokens.length) {
					keyboard.type(Key.ENTER);
				}
			}

			if (value.endsWith("${line.separator}")) {
				keyboard.type(Key.ENTER);
			}
		}
		else {
			keyboard.type(value);
		}
	}

	@Override
	public void sikuliUploadCommonFile(String image, String value)
		throws Exception {

		sikuliClick(image);

		Keyboard keyboard = new DesktopKeyboard();

		String fileName =
			FileUtil.getSeparator() + _TEST_DEPENDENCIES_DIR_NAME +
				FileUtil.getSeparator() + value;

		fileName = LiferaySeleniumUtil.getSourceDirFilePath(fileName);

		fileName = StringUtil.replace(fileName, "/", FileUtil.getSeparator());

		if (OSDetector.isApple()) {
			keyboard.keyDown(Key.CMD);
			keyboard.keyDown(Key.SHIFT);

			keyboard.type("g");

			keyboard.keyUp(Key.CMD);
			keyboard.keyUp(Key.SHIFT);

			sikuliType(image, fileName);

			keyboard.type(Key.ENTER);
		}
		else {
			keyboard.keyDown(Key.CTRL);

			keyboard.type("a");

			keyboard.keyUp(Key.CTRL);

			sikuliType(image, fileName);
		}

		pause("1000");

		keyboard.type(Key.ENTER);
	}

	@Override
	public void sikuliUploadTCatFile(String image, String value)
		throws Exception {

		String fileName = poshiProperties.tcatAdminRepository + "/" + value;

		fileName = FileUtil.fixFilePath(fileName);

		sikuliType(image, fileName);

		Keyboard keyboard = new DesktopKeyboard();

		pause("1000");

		keyboard.type(Key.ENTER);
	}

	@Override
	public void sikuliUploadTempFile(String image, String value)
		throws Exception {

		sikuliClick(image);

		Keyboard keyboard = new DesktopKeyboard();

		keyboard.keyDown(Key.CTRL);

		keyboard.type("a");

		keyboard.keyUp(Key.CTRL);

		String fileName = getOutputDirName() + "/" + value;

		fileName = FileUtil.fixFilePath(fileName);

		sikuliType(image, fileName);

		pause("1000");

		keyboard.type(Key.ENTER);
	}

	@Override
	public TargetLocator switchTo() {
		return _webDriver.switchTo();
	}

	@Override
	public void tripleClick(String locator) {
		Actions actions = new Actions(getWrappedWebDriver(locator));

		int count = 3;

		while (count > 0) {
			actions.click();

			count -= 1;
		}

		Action action = actions.build();

		action.perform();
	}

	@Override
	public void type(String locator, String value) {
		WebElement webElement = getWebElement(locator);

		if (!webElement.isEnabled()) {
			return;
		}

		int maxRetries = 3;
		int retryCount = 0;

		while (retryCount < maxRetries) {
			webElement.clear();

			if (retryCount == 0) {
				typeKeys(locator, value);
			}
			else {
				for (char c : value.toCharArray()) {
					typeKeys(locator, Character.toString(c));

					try {
						Thread.sleep(200);
					}
					catch (InterruptedException interruptedException) {
					}
				}
			}

			String webElementTagNametagName = webElement.getTagName();

			if (!webElementTagNametagName.equals("input")) {
				break;
			}

			String typedValue = webElement.getAttribute("value");

			if (typedValue.equals(value)) {
				return;
			}

			retryCount++;

			if (retryCount < maxRetries) {
				String message =
					"Actual typed value: '" + typedValue +
						"' did not match expected typed value: '" + value +
							"'.";

				System.out.println(
					message + " Retrying LiferaySelenium.type() attempt #" +
						(retryCount + 1) + ".");
			}
		}
	}

	@Override
	public void typeAceEditor(String locator, String value) {
		WebElement webElement = getWebElement(locator);

		String webElementTagName = webElement.getTagName();

		if (webElementTagName.equals("textarea")) {
			webElement.sendKeys(Keys.chord(Keys.CONTROL, "a"));

			webElement.sendKeys(Keys.DELETE);

			Matcher matcher = _aceEditorPattern.matcher(value);

			int x = 0;

			while (matcher.find()) {
				int y = matcher.start();

				String line = value.substring(x, y);

				webElement.sendKeys(line.trim());

				String specialCharacter = matcher.group();

				if (specialCharacter.equals("(")) {
					webElement.sendKeys("(");
				}
				else if (specialCharacter.equals("${line.separator}")) {
					keyPress(locator, "\\SPACE");
					keyPress(locator, "\\RETURN");
				}

				x = y + specialCharacter.length();
			}

			String line = value.substring(x);

			webElement.sendKeys(line.trim());

			webElement.sendKeys(Keys.chord(Keys.CONTROL, Keys.SHIFT, Keys.END));

			webElement.sendKeys(Keys.DELETE);

			return;
		}

		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver(webElement);

		StringBuilder sb = new StringBuilder();

		sb.append("ace.edit(");
		sb.append(getAttribute(locator + "@id"));
		sb.append(").setValue(\"");
		sb.append(HtmlUtil.escapeJS(StringUtil.replace(value, "\\", "\\\\")));
		sb.append("\");");

		javascriptExecutor.executeScript(sb.toString());
	}

	@Override
	public void typeAlert(String value) {
		Alert alert = getAlert();

		alert.sendKeys(value);
	}

	@Override
	public void typeCKEditor(String locator, String value) {
		StringBuilder sb = new StringBuilder();

		String idAttribute = getAttribute(locator + "@id");

		int x = idAttribute.indexOf("cke__");

		int y = idAttribute.indexOf("cke__", x + 1);

		if (y == -1) {
			y = idAttribute.length();
		}

		sb.append(idAttribute.substring(x + 4, y));

		sb.append(".setHTML(\"");
		sb.append(HtmlUtil.escapeJS(StringUtil.replace(value, "\\", "\\\\")));
		sb.append("\")");

		runScript(sb.toString());
	}

	@Override
	public void typeCodeMirrorEditor(String locator, String value)
		throws Exception {

		WebElement webElement = getWebElement(locator);

		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver(webElement);

		javascriptExecutor.executeScript(
			"arguments[0].CodeMirror.setValue(arguments[1]);", webElement,
			value);
	}

	@Override
	public void typeEditor(String locator, String value) {
		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver(locator);

		StringBuilder sb = new StringBuilder();

		sb.append("CKEDITOR.instances[\"");
		sb.append(getEditorName(locator));
		sb.append("\"].setData(\"");
		sb.append(HtmlUtil.escapeJS(StringUtil.replace(value, "\\", "\\\\")));
		sb.append("\");");

		javascriptExecutor.executeScript(sb.toString());
	}

	@Override
	public void typeKeys(String locator, String value) {
		WebElement webElement = getWebElement(locator);

		if (!webElement.isEnabled()) {
			return;
		}

		if (value.startsWith("keys=")) {
			value = value.substring(5);

			List<CharSequence> charSequences = new ArrayList<>();

			for (String key : value.split(",")) {
				CharSequence charSequence = key;

				if (_keysMap.containsKey(key)) {
					charSequence = _keysMap.get(key);
				}

				charSequences.add(charSequence);
			}

			webElement.sendKeys(charSequences.toArray(new CharSequence[0]));

			return;
		}

		if (value.contains("line-number=")) {
			value = value.replaceAll("line-number=\"\\d+\"", "");
		}

		int i = 0;

		Matcher matcher = _tabPattern.matcher(value);

		while (matcher.find()) {
			webElement.sendKeys(
				value.substring(matcher.start(), matcher.end() - 1));

			webElement.sendKeys(Keys.TAB);

			i = matcher.end();
		}

		webElement.sendKeys(value.substring(i));
	}

	@Override
	public void typeScreen(String value) {
		LiferaySeleniumUtil.typeScreen(value);
	}

	@Override
	public void uncheck(String locator) {
		WebElement webElement = getWebElement(locator);

		if (webElement.isSelected()) {
			webElement.click();
		}
	}

	@Override
	public void uploadCommonFile(String locator, String commonFilePath)
		throws Exception {

		String filePath =
			FileUtil.getSeparator() + getTestDependenciesDirName() +
				FileUtil.getSeparator() + commonFilePath;

		filePath = LiferaySeleniumUtil.getSourceDirFilePath(filePath);

		uploadFile(locator, FileUtil.fixFilePath(filePath));
	}

	@Override
	public void uploadFile(String locator, String filePath) {
		makeVisible(locator);

		WebElement webElement = getWebElement(locator);

		filePath = FileUtil.getCanonicalPath(filePath);

		if (filePath.endsWith(".jar") || filePath.endsWith(".lar") ||
			filePath.endsWith(".war") || filePath.endsWith(".zip")) {

			File file = new File(filePath);

			if (file.isDirectory()) {
				String archiveFilePath =
					getOutputDirName() + FileUtil.getSeparator() +
						file.getName();

				archiveFilePath = FileUtil.getCanonicalPath(archiveFilePath);

				ArchiveUtil.archive(filePath, archiveFilePath);

				filePath = archiveFilePath;
			}
		}

		filePath = FileUtil.fixFilePath(filePath);

		webElement.sendKeys(filePath);
	}

	@Override
	public void uploadTempFile(String location, String value) {
		String fileName = getOutputDirName() + FileUtil.getSeparator() + value;

		fileName = FileUtil.fixFilePath(fileName);

		uploadFile(location, fileName);
	}

	@Override
	public void verifyElementNotPresent(String locator) throws Exception {
		Condition elementNotPresentCondition = getElementNotPresentCondition(
			locator);

		elementNotPresentCondition.verify();
	}

	@Override
	public void verifyElementPresent(String locator) throws Exception {
		Condition elementPresentCondition = getElementPresentCondition(locator);

		elementPresentCondition.verify();
	}

	@Override
	public void verifyJavaScript(
			String javaScript, String message, String argument)
		throws Exception {

		Condition javaScriptCondition = getJavaScriptCondition(
			javaScript, message, argument);

		javaScriptCondition.verify();
	}

	@Override
	public void verifyNotVisible(String locator) throws Exception {
		Condition notVisibleCondition = getNotVisibleCondition(locator);

		notVisibleCondition.verify();
	}

	@Override
	public void verifyVisible(String locator) throws Exception {
		Condition visibleCondition = getVisibleCondition(locator);

		visibleCondition.verify();
	}

	@Override
	public void waitForConfirmation(String pattern) throws Exception {
		Condition confirmationCondition = getConfirmationCondition(pattern);

		confirmationCondition.waitFor();
	}

	@Override
	public void waitForConsoleTextNotPresent(String text) throws Exception {
		Condition consoleTextNotPresentCondition =
			getConsoleTextNotPresentCondition(text);

		consoleTextNotPresentCondition.waitFor();
	}

	@Override
	public void waitForConsoleTextPresent(String text) throws Exception {
		Condition consoleTextPresentCondition = getConsoleTextPresentCondition(
			text);

		consoleTextPresentCondition.waitFor();
	}

	@Override
	public void waitForEditable(String locator) throws Exception {
		Condition editableCondition = getEditableCondition(locator);

		editableCondition.waitFor();
	}

	@Override
	public void waitForElementNotPresent(String locator, String throwException)
		throws Exception {

		Condition elementNotPresentCondition = getElementNotPresentCondition(
			locator);

		elementNotPresentCondition.waitFor(throwException);
	}

	@Override
	public void waitForElementPresent(String locator, String throwException)
		throws Exception {

		Condition elementPresentCondition = getElementPresentCondition(locator);

		elementPresentCondition.waitFor(throwException);
	}

	@Override
	public void waitForJavaScript(
			String javaScript, String message, String argument)
		throws Exception {

		Condition javaScriptCondition = getJavaScriptCondition(
			javaScript, message, argument);

		javaScriptCondition.waitFor();
	}

	@Override
	public void waitForJavaScriptNoError(
			String javaScript, String message, String argument)
		throws Exception {

		Condition javaScriptCondition = getJavaScriptCondition(
			javaScript, message, argument);

		javaScriptCondition.waitFor("false");
	}

	@Override
	public void waitForNotEditable(String locator) throws Exception {
		Condition notEditableCondition = getNotEditableCondition(locator);

		notEditableCondition.waitFor();
	}

	@Override
	public void waitForNotPartialText(String locator, String value)
		throws Exception {

		Condition notPartialTextCondition = getNotPartialTextCondition(
			locator, value);

		notPartialTextCondition.waitFor();
	}

	@Override
	public void waitForNotSelectedLabel(String selectLocator, String pattern)
		throws Exception {

		Condition notSelectedLabelCondition = getNotSelectedLabelCondition(
			selectLocator, pattern);

		notSelectedLabelCondition.waitFor();
	}

	@Override
	public void waitForNotText(String locator, String value) throws Exception {
		Condition notTextCondition = getNotTextCondition(locator, value);

		notTextCondition.waitFor();
	}

	@Override
	public void waitForNotValue(String locator, String value) throws Exception {
		Condition notValueCondition = getNotValueCondition(locator, value);

		notValueCondition.waitFor();
	}

	@Override
	public void waitForNotVisible(String locator, String throwException)
		throws Exception {

		Condition notVisibleCondition = getNotVisibleCondition(locator);

		notVisibleCondition.waitFor(throwException);
	}

	@Override
	public void waitForNotVisibleInPage(String locator) throws Exception {
		Condition notVisibleInPageCondition = getNotVisibleInPageCondition(
			locator);

		notVisibleInPageCondition.waitFor();
	}

	@Override
	public void waitForNotVisibleInViewport(String locator) throws Exception {
		Condition notVisibleInViewportCondition =
			getNotVisibleInViewportCondition(locator);

		notVisibleInViewportCondition.waitFor();
	}

	@Override
	public void waitForPartialText(String locator, String value)
		throws Exception {

		Condition partialTextCondition = getPartialTextCondition(
			locator, value);

		partialTextCondition.waitFor();
	}

	@Override
	public void waitForPartialTextAceEditor(String locator, String value)
		throws Exception {

		Condition partialTextAceEditorCondition =
			getPartialTextAceEditorCondition(locator, value);

		partialTextAceEditorCondition.waitFor();
	}

	@Override
	public void waitForPartialTextCaseInsensitive(
			String locator, String pattern)
		throws Exception {

		Condition partialTextCaseInsensitiveCondition =
			getPartialTextCaseInsensitiveCondition(locator, pattern);

		partialTextCaseInsensitiveCondition.waitFor();
	}

	@Override
	public void waitForPopUp(String windowID, String timeout) {
		int wait = 0;

		if (timeout.equals("") || timeout.equals("null")) {
			wait = 30;
		}
		else {
			wait = GetterUtil.getInteger(timeout) / 1000;
		}

		if (windowID.equals("") || windowID.equals("null")) {
			for (int i = 0; i <= wait; i++) {
				Set<String> windowHandles = getWindowHandles();

				if (windowHandles.size() > 1) {
					return;
				}

				try {
					Thread.sleep(1000);
				}
				catch (Exception exception) {
				}
			}
		}
		else {
			String targetWindowTitle = windowID;

			if (targetWindowTitle.startsWith("title=")) {
				targetWindowTitle = targetWindowTitle.substring(6);
			}

			for (int i = 0; i <= wait; i++) {
				for (String windowHandle : getWindowHandles()) {
					WebDriver.TargetLocator targetLocator = switchTo();

					targetLocator.window(windowHandle);

					if (targetWindowTitle.equals(getTitle())) {
						targetLocator.window(getDefaultWindowHandle());

						return;
					}
				}

				try {
					Thread.sleep(1000);
				}
				catch (Exception exception) {
				}
			}
		}

		TestCase.fail("Unable to find the window ID \"" + windowID + "\"");
	}

	@Override
	public void waitForSelectedLabel(String selectLocator, String pattern)
		throws Exception {

		Condition selectedLabelCondition = getSelectedLabelCondition(
			selectLocator, pattern);

		selectedLabelCondition.waitFor();
	}

	@Override
	public void waitForText(String locator, String value) throws Exception {
		Condition textCondition = getTextCondition(locator, value);

		textCondition.waitFor();
	}

	@Override
	public void waitForTextCaseInsensitive(String locator, String pattern)
		throws Exception {

		Condition textCaseInsensitiveCondition =
			getTextCaseInsensitiveCondition(locator, pattern);

		textCaseInsensitiveCondition.waitFor();
	}

	@Override
	public void waitForTextMatches(String locator, String regex)
		throws Exception {

		Condition textMatchesCondition = getTextMatchesCondition(
			locator, regex);

		textMatchesCondition.waitFor();
	}

	@Override
	public void waitForTextNotPresent(String value) throws Exception {
		Condition textNotPresentCondition = getTextNotPresentCondition(value);

		textNotPresentCondition.waitFor();
	}

	@Override
	public void waitForTextPresent(String value) throws Exception {
		Condition textPresentCondition = getTextPresentCondition(value);

		textPresentCondition.waitFor();
	}

	@Override
	public void waitForValue(String locator, String value) throws Exception {
		Condition valueConditionCondition = getValueCondition(locator, value);

		valueConditionCondition.waitFor();
	}

	@Override
	public void waitForVisible(String locator, String throwException)
		throws Exception {

		Condition visibleCondition = getVisibleCondition(locator);

		visibleCondition.waitFor(throwException);
	}

	@Override
	public void waitForVisibleInPage(String locator) throws Exception {
		Condition visibleInPageCondition = getVisibleInPageCondition(locator);

		visibleInPageCondition.waitFor();
	}

	@Override
	public void waitForVisibleInViewport(String locator) throws Exception {
		Condition visibleInViewportCondition = getVisibleInViewportCondition(
			locator);

		visibleInViewportCondition.waitFor();
	}

	protected void acceptConfirmation() {
		WebDriver.TargetLocator targetLocator = switchTo();

		Alert alert = targetLocator.alert();

		alert.accept();
	}

	protected void executeJavaScriptEvent(
		String locator, String eventType, String event) {

		WebElement webElement = getWebElement(locator);

		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver(webElement);

		if (!webElement.isDisplayed()) {
			scrollWebElementIntoView(webElement);
		}

		StringBuilder sb = new StringBuilder(6);

		sb.append("var element = arguments[0];");
		sb.append("var event = document.createEvent('");
		sb.append(eventType);
		sb.append("');event.initEvent('");
		sb.append(event);
		sb.append("', true, false);element.dispatchEvent(event);");

		javascriptExecutor.executeScript(sb.toString(), webElement);
	}

	protected Alert getAlert() {
		if (_alert == null) {
			switchTo();

			WebDriverWait webDriverWait = new WebDriverWait(
				this, Duration.ofSeconds(1));

			_alert = webDriverWait.until(ExpectedConditions.alertIsPresent());
		}

		return _alert;
	}

	protected String getAlertText() {
		switchTo();

		WebDriverWait webDriverWait = new WebDriverWait(
			this, Duration.ofSeconds(1));

		Alert alert = webDriverWait.until(ExpectedConditions.alertIsPresent());

		return alert.getText();
	}

	protected By getBy(String locator) {
		return LiferaySeleniumUtil.getBy(locator);
	}

	protected Condition getConfirmationCondition(String pattern) {
		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Expected text \"", pattern,
						"\" does not match actual text \"",
						getConfirmation(null), "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				return pattern.equals(getConfirmation(null));
			}

		};
	}

	protected Condition getConsoleTextNotPresentCondition(String text) {
		String message = "\"" + text + "\" is present in console";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				return !LiferaySeleniumUtil.isConsoleTextPresent(text);
			}

		};
	}

	protected Condition getConsoleTextPresentCondition(String text) {
		String message = "\"" + text + "\" is not present in console";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				return LiferaySeleniumUtil.isConsoleTextPresent(text);
			}

		};
	}

	protected String getCSSSource(String htmlSource) throws Exception {
		org.jsoup.nodes.Document htmlDocument = Jsoup.parse(htmlSource);

		Elements elements = htmlDocument.select("link[type=text/css]");

		StringBuilder sb = new StringBuilder();

		for (Element element : elements) {
			String href = element.attr("href");

			if (!href.contains(poshiProperties.portalURL)) {
				href = poshiProperties.portalURL + href;
			}

			Connection connection = Jsoup.connect(href);

			org.jsoup.nodes.Document document = connection.get();

			sb.append(document.text());

			sb.append("\n");
		}

		return sb.toString();
	}

	protected String getDefaultWindowHandle() {
		return _defaultWindowHandle;
	}

	protected Condition getEditableCondition(String locator) {
		String message = "Element is not editable at \"" + locator + "\"";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				WebElement webElement = getWebElement(locator);

				return webElement.isEnabled();
			}

		};
	}

	protected String getEditorName(String locator) {
		String titleAttribute = getAttribute(locator + "@title");

		if (titleAttribute.contains("Rich Text Editor")) {
			int x = titleAttribute.indexOf(",");

			int y = titleAttribute.indexOf(",", x + 1);

			if (y == -1) {
				y = titleAttribute.length();
			}

			return titleAttribute.substring(x + 2, y);
		}

		String idAttribute = getAttribute(locator + "@id");

		if (idAttribute.contains("cke__")) {
			int x = idAttribute.indexOf("cke__");

			int y = idAttribute.indexOf("cke__", x + 1);

			if (y == -1) {
				y = idAttribute.length();
			}

			return idAttribute.substring(x + 4, y);
		}

		return idAttribute;
	}

	protected Condition getElementFocusedCondition(String locator) {
		String message = "Element from locator " + locator + " is not focused";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				WebElement webElement = getWebElement(locator);

				WebDriver webDriver = getWebDriver();

				TargetLocator targetLocator = webDriver.switchTo();

				WebElement activeWebElement = targetLocator.activeElement();

				return webElement.equals(activeWebElement);
			}

		};
	}

	protected Condition getElementNotFocusedCondition(String locator) {
		String message = "Element from locator " + locator + " is not focused";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				return !isElementFocused(locator);
			}

		};
	}

	protected Condition getElementNotPresentCondition(String locator) {
		String message = "Element is present at \"" + locator + "\"";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				return !isElementPresent(locator);
			}

		};
	}

	protected long getElementPositionBottom(String locator) {
		return getElementPositionTop(locator) + getElementHeight(locator);
	}

	protected long getElementPositionCenterX(String locator) {
		return getElementPositionLeft(locator) + (getElementWidth(locator) / 2);
	}

	protected long getElementPositionCenterY(String locator) {
		return getElementPositionTop(locator) + (getElementHeight(locator) / 2);
	}

	protected int getElementPositionLeft(String locator) {
		WebElement webElement = getWebElement(locator);

		Point point = webElement.getLocation();

		return point.getX();
	}

	protected long getElementPositionRight(String locator) {
		return getElementPositionLeft(locator) + getElementWidth(locator);
	}

	protected int getElementPositionTop(String locator) {
		WebElement webElement = getWebElement(locator);

		Point point = webElement.getLocation();

		return point.getY();
	}

	protected Condition getElementPresentCondition(String locator) {
		String message = "Element is not present at \"" + locator + "\"";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				List<WebElement> webElements = getWebElements(locator);

				return !webElements.isEmpty();
			}

		};
	}

	protected Point getFramePoint() {
		int x = 0;
		int y = 0;

		WebDriver wrappedWebDriver = getWrappedWebDriver("//body");

		WebDriver.TargetLocator targetLocator = wrappedWebDriver.switchTo();

		targetLocator.window(_defaultWindowHandle);

		for (WebElement webElement : _frameWebElements) {
			Point point = webElement.getLocation();

			x += point.getX();
			y += point.getY();

			targetLocator.frame(webElement);
		}

		return new Point(x, y);
	}

	protected int getFramePositionLeft() {
		Point point = getFramePoint();

		return point.getX();
	}

	protected int getFramePositionTop() {
		Point point = getFramePoint();

		return point.getY();
	}

	protected Stack<WebElement> getFrameWebElements() {
		return _frameWebElements;
	}

	protected Table getHTMLTable(String locator) {
		List<List<String>> table = new ArrayList<>();

		List<WebElement> rowWebElements = findElements(
			By.xpath(locator + "//tr"));

		for (int i = 2; i <= rowWebElements.size(); i++) {
			List<String> webElementTexts = new ArrayList<>();

			List<WebElement> columnWebElements = findElements(
				By.xpath(locator + "//tr[" + i + "]//td"));

			for (int j = 1; j <= columnWebElements.size(); j++) {
				WebElement webElement = findElement(
					By.xpath(locator + "//tr[" + i + "]//td[" + j + "]"));

				webElementTexts.add(webElement.getText());
			}

			table.add(webElementTexts);
		}

		return new DefaultTable(table);
	}

	protected ImageTarget getImageTarget(String image) throws Exception {
		String fileName =
			FileUtil.getSeparator() + getSikuliImagesDirName() + image;

		File file = new File(
			LiferaySeleniumUtil.getSourceDirFilePath(fileName));

		return new ImageTarget(file);
	}

	protected Condition getJavaScriptCondition(
		String javaScript, String message, String argument) {

		return new Condition(message) {

			@Override
			public boolean evaluate() {
				JavascriptExecutor javascriptExecutor =
					(JavascriptExecutor)getWrappedWebDriver("//body");

				Object object = null;

				try {
					if (Validator.isNotNull(argument)) {
						object = getWebElement(argument);
					}
				}
				catch (ElementNotFoundPoshiRunnerException |
					   InvalidSelectorException | NullPointerException
						   exception) {

					object = argument;
				}

				Boolean javaScriptResult =
					(Boolean)javascriptExecutor.executeScript(
						javaScript, object);

				if (javaScriptResult == null) {
					return false;
				}

				return javaScriptResult;
			}

		};
	}

	protected int getNavigationBarHeight() {
		return _navigationBarHeight;
	}

	protected Condition getNotEditableCondition(String locator) {
		String message = "Element is editable at \"" + locator + "\"";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				return !isEditable(locator);
			}

		};
	}

	protected Condition getNotPartialTextCondition(
		String locator, String value) {

		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"\"", getText(locator), "\" contains \"", value,
						"\" at \"", locator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				return !isPartialText(locator, value);
			}

		};
	}

	protected Condition getNotSelectedLabelCondition(
		String selectLocator, String pattern) {

		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (isSelectedLabel(selectLocator, pattern)) {
					String message = StringUtil.combine(
						"Pattern \"", pattern, "\" matches \"",
						getSelectedLabel(selectLocator), "\" at \"",
						selectLocator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				if (isElementNotPresent(selectLocator)) {
					return false;
				}

				List<String> selectedLabelsList = Arrays.asList(
					getSelectedLabels(selectLocator));

				return !selectedLabelsList.contains(pattern);
			}

		};
	}

	protected Condition getNotTextCondition(String locator, String value) {
		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Pattern \"", value, "\" matches \"", getText(locator),
						"\" at \"", locator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				return !isText(locator, value);
			}

		};
	}

	protected Condition getNotValueCondition(String locator, String value) {
		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Pattern \"", value, "\" matches \"",
						getElementValue(locator), "\" at \"", locator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				return !isValue(locator, value);
			}

		};
	}

	protected Condition getNotVisibleCondition(String locator) {
		String message = "Element is visible at \"" + locator + "\"";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				return !isVisible(locator);
			}

		};
	}

	protected Condition getNotVisibleInPageCondition(String locator) {
		String message = "Element is visible in page at \"" + locator + "\"";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				return !isVisibleInPage(locator);
			}

		};
	}

	protected Condition getNotVisibleInViewportCondition(String locator) {
		String message =
			"Element is visible in viewport at \"" + locator + "\"";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				return !isVisibleInViewport(locator);
			}

		};
	}

	protected Condition getPartialTextAceEditorCondition(
		String locator, String value) {

		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Actual text \"", getTextAceEditor(locator),
						"\" does not contain expected text \"", value,
						"\" at \"", locator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				WebElement webElement = getWebElement(locator);

				String text = webElement.getText();

				text = StringUtil.replace(text, "\n", "");

				return text.contains(value);
			}

		};
	}

	protected Condition getPartialTextCaseInsensitiveCondition(
		String locator, String value) {

		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Actual text \"", getText(locator),
						"\" does not contain expected text (case insensitive) ",
						"\"", value, "\" at \"", locator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				String actual = StringUtil.toUpperCase(getText(locator));

				return actual.contains(StringUtil.toUpperCase(value));
			}

		};
	}

	protected Condition getPartialTextCondition(String locator, String value) {
		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Actual text \"", getText(locator),
						"\" does not contain expected text \"", value,
						"\" at \"", locator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				WebElement webElement = getWebElement(locator);

				String text = webElement.getText();

				return text.contains(value);
			}

		};
	}

	protected int getScrollOffsetX() {
		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver("//body");

		Object pageXOffset = javascriptExecutor.executeScript(
			"return window.pageXOffset;");

		return GetterUtil.getInteger(pageXOffset);
	}

	protected int getScrollOffsetY() {
		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver("//body");

		Object pageYOffset = javascriptExecutor.executeScript(
			"return window.pageYOffset;");

		return GetterUtil.getInteger(pageYOffset);
	}

	protected Condition getSelectedLabelCondition(
		String selectLocator, String pattern) {

		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (isNotSelectedLabel(selectLocator, pattern)) {
					String message = StringUtil.combine(
						"Expected text \"", pattern,
						"\" does not match actual text \"",
						getSelectedLabel(selectLocator), "\" at \"",
						selectLocator + "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				if (isElementNotPresent(selectLocator)) {
					return false;
				}

				return pattern.equals(getSelectedLabel(selectLocator));
			}

		};
	}

	protected Condition getTextCaseInsensitiveCondition(
		String locator, String value) {

		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Expected text \"", value,
						"\" does not match actual text (case insensitive) \"",
						getText(locator), "\" at \"", locator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				String actual = StringUtil.toUpperCase(getText(locator));

				return actual.equals(StringUtil.toUpperCase(value));
			}

		};
	}

	protected Condition getTextCondition(String locator, String value) {
		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Expected text \"", value,
						"\" does not match actual text \"", getText(locator),
						"\" at \"", locator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				return value.equals(getText(locator));
			}

		};
	}

	protected Condition getTextMatchesCondition(String locator, String regex) {
		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Actual text \"", getText(locator),
						"\" does not match pattern \"", regex, "\" at \"",
						locator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				String text = getText(locator);

				return text.matches(regex);
			}

		};
	}

	protected Condition getTextNotPresentCondition(String pattern) {
		String message = "\"" + pattern + "\" is present";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				return !isTextPresent(pattern);
			}

		};
	}

	protected Condition getTextPresentCondition(String pattern) {
		String message = "\"" + pattern + "\" is not present";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				WebElement webElement = getWebElement("//body");

				String text = webElement.getText();

				return text.contains(pattern);
			}

		};
	}

	protected Condition getValueCondition(String locator, String value) {
		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Expected text \"", value,
						"\" does not match actual text \"",
						getElementValue(locator), "\" at \"", locator, "\"");

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				return value.equals(getElementValue(locator));
			}

		};
	}

	protected Condition getValueMatchCondition(String locator, String regex) {
		return new Condition() {

			@Override
			public void assertTrue() throws Exception {
				if (!evaluate()) {
					String message = StringUtil.combine(
						"Actual value \"", getElementValue(locator), "\" at \"",
						locator, "\"", "\" does not match pattern\"", regex);

					throw new Exception(message);
				}
			}

			@Override
			public boolean evaluate() throws Exception {
				String value = getElementValue(locator);

				return value.matches(regex);
			}

		};
	}

	protected int getViewportHeight() {
		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver("//body");

		return GetterUtil.getInteger(
			javascriptExecutor.executeScript("return window.innerHeight;"));
	}

	protected int getViewportPositionBottom() {
		return getScrollOffsetY() + getViewportHeight();
	}

	protected Condition getVisibleCondition(String locator) {
		String message = "Element is not visible at \"" + locator + "\"";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				return isVisibleInPage(locator);
			}

		};
	}

	protected Condition getVisibleInPageCondition(String locator) {
		String message =
			"Element is not visible in page at \"" + locator + "\"";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				WebElement webElement = getWebElement(locator);

				scrollWebElementIntoView(webElement);

				return webElement.isDisplayed();
			}

		};
	}

	protected Condition getVisibleInViewportCondition(String locator) {
		String message =
			"Element is not visible in viewport at \"" + locator + "\"";

		return new Condition(message) {

			@Override
			public boolean evaluate() throws Exception {
				WebElement webElement = getWebElement(locator);

				return webElement.isDisplayed();
			}

		};
	}

	protected WebDriver getWebDriver() {
		return _webDriver;
	}

	protected WebElement getWebElement(String locator) {
		return getWebElement(locator, null);
	}

	protected WebElement getWebElement(String locator, String timeout) {
		List<WebElement> webElements = getWebElements(locator, timeout);

		if (!webElements.isEmpty()) {
			return webElements.get(0);
		}

		throw new ElementNotFoundPoshiRunnerException(
			"Element is not present at \"" + locator + "\"");
	}

	protected List<WebElement> getWebElements(String locator) {
		return getWebElements(locator, null);
	}

	protected List<WebElement> getWebElements(String locator, String timeout) {
		if (timeout != null) {
			setTimeoutImplicit(timeout);
		}

		try {
			List<WebElement> webElements = new ArrayList<>();

			for (WebElement webElement : findElements(getBy(locator))) {
				webElements.add(new RetryWebElementImpl(locator, webElement));
			}

			return webElements;
		}
		finally {
			if (timeout != null) {
				setDefaultTimeoutImplicit();
			}
		}
	}

	protected Point getWindowPoint() {
		WebDriver wrappedWebDriver = getWrappedWebDriver("//body");

		WebDriver.Options options = wrappedWebDriver.manage();

		WebDriver.Window window = options.window();

		return window.getPosition();
	}

	protected int getWindowPositionLeft() {
		Point point = getWindowPoint();

		return point.getX();
	}

	protected int getWindowPositionTop() {
		Point point = getWindowPoint();

		return point.getY();
	}

	protected WebDriver getWrappedWebDriver(String locator) {
		WebDriverWait webDriverWait = new WebDriverWait(
			this, Duration.ofSeconds(5));

		webDriverWait.until(
			ExpectedConditions.presenceOfElementLocated(getBy(locator)));

		return getWrappedWebDriver(getWebElement(locator));
	}

	protected WebDriver getWrappedWebDriver(WebElement webElement) {
		WrapsDriver wrapsDriver = (WrapsDriver)webElement;

		return wrapsDriver.getWrappedDriver();
	}

	protected boolean isObscured(WebElement webElement) {
		JavascriptExecutor javascriptExecutor =
			(JavascriptExecutor)getWrappedWebDriver(webElement);

		StringBuilder sb = new StringBuilder();

		sb.append("var element = arguments[0];");
		sb.append("console.log(element);");
		sb.append("var rect = element.getBoundingClientRect();");
		sb.append("elementX = (rect.right + rect.left) / 2;");
		sb.append("elementY = (rect.top + rect.bottom) / 2;");
		sb.append("var newElement = ");
		sb.append("document.elementFromPoint(elementX, elementY);");
		sb.append("if (element == newElement) {");
		sb.append("return false;}");
		sb.append("return true;");

		Boolean obscured = (Boolean)javascriptExecutor.executeScript(
			sb.toString(), webElement);

		return obscured.booleanValue();
	}

	protected boolean isValidKeycode(String keycode) {
		for (Keys keys : Keys.values()) {
			String keysName = keys.name();

			if (keysName.equals(keycode)) {
				return true;
			}
		}

		return false;
	}

	protected void moveToElement(
		Actions actions, WebElement webElement, String offset) {

		String[] offsetCoordinates = offset.split(",");

		int x = GetterUtil.getInteger(offsetCoordinates[0]);
		int y = GetterUtil.getInteger(offsetCoordinates[1]);

		actions.moveToElement(webElement, x, y);
	}

	protected void ocularConfig() {
		String testBaseDirName = poshiProperties.testBaseDirName;

		OcularConfiguration ocularConfiguration = Ocular.config();

		ocularConfiguration = ocularConfiguration.snapshotPath(
			Paths.get(testBaseDirName, getOcularBaselineImageDirName()));

		FileUtil.delete(
			new File(testBaseDirName, getOcularResultImageDirName()));

		ocularConfiguration.resultPath(
			Paths.get(testBaseDirName, getOcularResultImageDirName()));

		ocularConfiguration.globalSimilarity(99);

		ocularConfiguration.saveSnapshot(true);
	}

	protected void saveWebPage(String fileName, String htmlSource)
		throws Exception {

		if (!poshiProperties.saveWebPage) {
			return;
		}

		StringBuilder sb = new StringBuilder(3);

		sb.append("<style>");
		sb.append(getCSSSource(htmlSource));
		sb.append("</style></html>");

		FileUtil.write(
			fileName,
			StringUtil.replace(htmlSource, "<\\html>", sb.toString()));
	}

	protected void scrollWebElementIntoView(WebElement webElement) {
		if (!webElement.isDisplayed() || isObscured(webElement)) {
			JavascriptExecutor javascriptExecutor =
				(JavascriptExecutor)getWrappedWebDriver(webElement);

			javascriptExecutor.executeScript(
				"arguments[0].scrollIntoView(false);", webElement);
		}
	}

	protected void selectByRegexpText(String selectLocator, String regexp) {
		Select select = new Select(getWebElement(selectLocator));

		List<WebElement> optionWebElements = select.getOptions();

		Pattern pattern = Pattern.compile(regexp);

		int index = -1;

		for (WebElement optionWebElement : optionWebElements) {
			String optionWebElementText = optionWebElement.getText();

			Matcher matcher = pattern.matcher(optionWebElementText);

			if (matcher.matches()) {
				index = optionWebElements.indexOf(optionWebElement);

				break;
			}
		}

		select.selectByIndex(index);
	}

	protected void selectByRegexpValue(String selectLocator, String regexp) {
		Select select = new Select(getWebElement(selectLocator));

		List<WebElement> optionWebElements = select.getOptions();

		Pattern pattern = Pattern.compile(regexp);

		int index = -1;

		for (WebElement optionWebElement : optionWebElements) {
			String optionWebElementValue = optionWebElement.getAttribute(
				"value");

			Matcher matcher = pattern.matcher(optionWebElementValue);

			if (matcher.matches()) {
				index = optionWebElements.indexOf(optionWebElement);

				break;
			}
		}

		select.selectByIndex(index);
	}

	protected void setAlert(Alert alert) {
		_alert = alert;
	}

	protected void setDefaultWindowHandle(String defaultWindowHandle) {
		_defaultWindowHandle = defaultWindowHandle;
	}

	protected void setNavigationBarHeight(int navigationBarHeight) {
		_navigationBarHeight = navigationBarHeight;
	}

	protected PoshiProperties poshiProperties;

	protected abstract class Condition {

		public Condition() {
			this("");
		}

		public Condition(String message) {
			_message = message;
		}

		public void assertTrue() throws Exception {
			if (!evaluate()) {
				throw new Exception(_message);
			}
		}

		public abstract boolean evaluate() throws Exception;

		public void verify() throws Exception {
			if (!evaluate()) {
				throw new PoshiRunnerWarningException(
					"VERIFICATION_WARNING: " + _message);
			}
		}

		public void waitFor() throws Exception {
			waitFor("true");
		}

		public void waitFor(String throwException) throws Exception {
			int timeout = poshiProperties.timeoutExplicitWait * 1000;
			int wait = 500;

			for (int millisecond = 0; millisecond < timeout;
				 millisecond += wait) {

				try {
					if (evaluate()) {
						return;
					}
				}
				catch (Exception exception) {
				}

				Thread.sleep(wait);
			}

			if ((throwException == null) ||
				Boolean.parseBoolean(throwException)) {

				assertTrue();
			}
		}

		private final String _message;

	}

	private Dimension _getDimension(String size) {
		String[] sizeParts = StringUtil.split(size, "[\\D]+");

		int x = GetterUtil.getInteger(sizeParts[0]);
		int y = GetterUtil.getInteger(sizeParts[1]);

		return new Dimension(x, y);
	}

	private static final String _OCULAR_BASELINE_IMAGE_DIR_NAME;

	private static final String _OCULAR_RESULT_IMAGE_DIR_NAME;

	private static final String _OUTPUT_DIR_NAME;

	private static final String _SIKULI_IMAGES_DIR_NAME;

	private static final String _TEST_DEPENDENCIES_DIR_NAME;

	private static final Pattern _aceEditorPattern = Pattern.compile(
		"\\(|\\$\\{line\\.separator\\}");
	private static final Pattern _coordinatePairsPattern = Pattern.compile(
		"[+-]?\\d+\\,[+-]?\\d+(\\|[+-]?\\d+\\,[+-]?\\d+)*");
	private static final Map<String, Integer> _keyCodeMap =
		new Hashtable<String, Integer>() {
			{
				put("ALT", Integer.valueOf(KeyEvent.VK_ALT));
				put("COMMAND", Integer.valueOf(KeyEvent.VK_META));
				put("CONTROL", Integer.valueOf(KeyEvent.VK_CONTROL));
				put("CTRL", Integer.valueOf(KeyEvent.VK_CONTROL));
				put("SHIFT", Integer.valueOf(KeyEvent.VK_SHIFT));
			}
		};

	private static final Map<String, Keys> _keysMap =
		new Hashtable<String, Keys>() {
			{
				for (Keys keys : Keys.class.getEnumConstants()) {
					put(keys.name(), keys);
				}

				put("CTRL", Keys.CONTROL);
			}
		};

	private static final Pattern _tabPattern = Pattern.compile(
		".*?(\\t).*?", Pattern.DOTALL);

	static {
		PoshiProperties poshiProperties = PoshiProperties.getPoshiProperties();

		String testDependenciesDirName =
			poshiProperties.testDependenciesDirName;

		String ocularResultImageDirName =
			testDependenciesDirName + "//ocular//result";
		String ocularBaselineImageDirName =
			testDependenciesDirName + "//ocular//baseline";
		String sikuliImagesDirName =
			testDependenciesDirName + "//sikuli//linux//";

		String outputDirName = poshiProperties.outputDirName;

		if (OSDetector.isApple()) {
			sikuliImagesDirName = StringUtil.replace(
				sikuliImagesDirName, "linux", "osx");
		}
		else if (OSDetector.isWindows()) {
			ocularResultImageDirName = StringUtil.replace(
				ocularResultImageDirName, "//", "\\");
			ocularBaselineImageDirName = StringUtil.replace(
				ocularBaselineImageDirName, "//", "\\");
			outputDirName = StringUtil.replace(outputDirName, "//", "\\");
			sikuliImagesDirName = StringUtil.replace(
				sikuliImagesDirName, "//", "\\");
			sikuliImagesDirName = StringUtil.replace(
				sikuliImagesDirName, "linux", "windows");

			testDependenciesDirName = StringUtil.replace(
				testDependenciesDirName, "//", "\\");
		}

		_OUTPUT_DIR_NAME = outputDirName;
		_OCULAR_RESULT_IMAGE_DIR_NAME = ocularResultImageDirName;
		_OCULAR_BASELINE_IMAGE_DIR_NAME = ocularBaselineImageDirName;
		_SIKULI_IMAGES_DIR_NAME = sikuliImagesDirName;
		_TEST_DEPENDENCIES_DIR_NAME = testDependenciesDirName;
	}

	private Alert _alert;
	private String _clipBoard = "";
	private String _defaultWindowHandle;
	private final Stack<WebElement> _frameWebElements = new Stack<>();
	private int _navigationBarHeight = 120;
	private String _primaryTestSuiteName;
	private String _testName;
	private int _totalPauseDuration;
	private final WebDriver _webDriver;

	private class LocationCallable implements Callable<String> {

		@Override
		public String call() throws Exception {
			return _webDriver.getCurrentUrl();
		}

		private Callable<String> _init(WebDriver webDriver) throws Exception {
			_webDriver = webDriver;

			return this;
		}

		private WebDriver _webDriver;

	}

}
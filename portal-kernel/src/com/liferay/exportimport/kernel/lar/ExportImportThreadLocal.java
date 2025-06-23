/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Michael C. Han
 */
public class ExportImportThreadLocal {

	public static Long getExportImportConfigurationId() {
		return _exportImportConfigurationId.get();
	}

	public static boolean isDataDeletionImportInProcess() {
		if (isLayoutDataDeletionImportInProcess() ||
			isPortletDataDeletionImportInProcess()) {

			return true;
		}

		return false;
	}

	public static boolean isExportInProcess() {
		if (isLayoutExportInProcess() || isPortletExportInProcess()) {
			return true;
		}

		return false;
	}

	public static boolean isImportInProcess() {
		if (isDataDeletionImportInProcess() || isLayoutImportInProcess() ||
			isLayoutValidationInProcess() || isPortletImportInProcess() ||
			isPortletValidationInProcess()) {

			return true;
		}

		return false;
	}

	public static boolean isInitialLayoutStagingInProcess() {
		return _initialLayoutStagingInProcess.get();
	}

	public static boolean isLayoutDataDeletionImportInProcess() {
		return _layoutDataDeletionImportInProcess.get();
	}

	public static boolean isLayoutExportInProcess() {
		return _layoutExportInProcess.get();
	}

	public static boolean isLayoutImportInProcess() {
		return _layoutImportInProcess.get();
	}

	public static boolean isLayoutStagingInProcess() {
		return _layoutStagingInProcess.get();
	}

	public static boolean isLayoutValidationInProcess() {
		return _layoutValidationInProcess.get();
	}

	public static boolean isPortletDataDeletionImportInProcess() {
		return _portletDataDeletionImportInProcess.get();
	}

	public static boolean isPortletExportInProcess() {
		return _portletExportInProcess.get();
	}

	public static boolean isPortletImportInProcess() {
		return _portletImportInProcess.get();
	}

	public static boolean isPortletStagingInProcess() {
		return _portletStagingInProcess.get();
	}

	public static boolean isPortletValidationInProcess() {
		return _portletValidationInProcess.get();
	}

	public static boolean isStagingInProcess() {
		if (isLayoutStagingInProcess() || isPortletStagingInProcess() ||
			isStagingInProcessOnRemoteLive()) {

			return true;
		}

		return false;
	}

	public static boolean isStagingInProcessOnRemoteLive() {
		return _stagingInProcessOnRemoteLive.get();
	}

	public static void setExportImportConfigurationId(
		long exportImportConfigurationId) {

		_exportImportConfigurationId.set(exportImportConfigurationId);
	}

	public static void setInitialLayoutStagingInProcess(
		boolean initialLayoutStagingInProcess) {

		_initialLayoutStagingInProcess.set(initialLayoutStagingInProcess);
	}

	public static void setLayoutDataDeletionImportInProcess(
		boolean layoutDataDeletionImportInProcess) {

		_layoutDataDeletionImportInProcess.set(
			layoutDataDeletionImportInProcess);
	}

	public static void setLayoutExportInProcess(boolean layoutExportInProcess) {
		_layoutExportInProcess.set(layoutExportInProcess);
	}

	public static void setLayoutImportInProcess(boolean layoutImportInProcess) {
		_layoutImportInProcess.set(layoutImportInProcess);
	}

	public static void setLayoutStagingInProcess(
		boolean layoutStagingInProcess) {

		_layoutStagingInProcess.set(layoutStagingInProcess);
	}

	public static void setLayoutValidationInProcess(
		boolean layoutValidationInProcess) {

		_layoutValidationInProcess.set(layoutValidationInProcess);
	}

	public static void setPortletDataDeletionImportInProcess(
		boolean portletDataDeletionImportInProcess) {

		_portletDataDeletionImportInProcess.set(
			portletDataDeletionImportInProcess);
	}

	public static void setPortletExportInProcess(
		boolean portletExportInProcess) {

		_portletExportInProcess.set(portletExportInProcess);
	}

	public static void setPortletImportInProcess(
		boolean portletImportInProcess) {

		_portletImportInProcess.set(portletImportInProcess);
	}

	public static void setPortletStagingInProcess(
		boolean portletStagingInProcess) {

		_portletStagingInProcess.set(portletStagingInProcess);
	}

	public static void setPortletValidationInProcess(
		boolean portletValidationInProcess) {

		_portletValidationInProcess.set(portletValidationInProcess);
	}

	public static void setStagingInProcessOnRemoteLive(
		boolean stagingInProcessOnRemoteLive) {

		_stagingInProcessOnRemoteLive.set(stagingInProcessOnRemoteLive);
	}

	private static final ThreadLocal<Long> _exportImportConfigurationId =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._exportImportConfigurationId",
			() -> 0L);
	private static final ThreadLocal<Boolean> _initialLayoutStagingInProcess =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._initialLayoutStagingInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean>
		_layoutDataDeletionImportInProcess = new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class +
				"._layoutDataDeletionImportInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _layoutExportInProcess =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._layoutExportInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _layoutImportInProcess =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._layoutImportInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _layoutStagingInProcess =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._layoutStagingInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _layoutValidationInProcess =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._layoutValidationInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean>
		_portletDataDeletionImportInProcess = new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class +
				"._portletDataDeletionImportInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _portletExportInProcess =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._portletExportInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _portletImportInProcess =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._portletImportInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _portletStagingInProcess =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._portletStagingInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _portletValidationInProcess =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._portletValidationInProcess",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _stagingInProcessOnRemoteLive =
		new CentralizedThreadLocal<>(
			ExportImportThreadLocal.class + "._stagingInProcessOnRemoteLive",
			() -> Boolean.FALSE);

}
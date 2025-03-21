/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import com.liferay.petra.function.UnsafeSupplier;

import jakarta.portlet.PortletURL;

import java.util.Map;

/**
 * @author Carlos Lancha
 */
public class TabsItemBuilder {

	public static AfterPutDataStep putData(String key, String value) {
		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.putData(key, value);
	}

	public static AfterPutDataStep putData(
		String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.putData(key, valueUnsafeSupplier);
	}

	public static AfterActiveStep setActive(boolean active) {
		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.setActive(active);
	}

	public static AfterActiveStep setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.setActive(activeUnsafeSupplier);
	}

	public static AfterSetDataStep setData(Map<String, Object> data) {
		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.setData(data);
	}

	public static AfterDisabledStep setDisabled(boolean disabled) {
		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.setDisabled(disabled);
	}

	public static AfterDisabledStep setDisabled(
		UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.setDisabled(disabledUnsafeSupplier);
	}

	public static AfterHrefStep setHref(Object href) {
		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.setHref(href);
	}

	public static AfterHrefStep setHref(
		PortletURL portletURL, Object... parameters) {

		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.setHref(parameters);
	}

	public static AfterHrefStep setHref(
		UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.setHref(hrefUnsafeSupplier);
	}

	public static AfterLabelStep setLabel(String label) {
		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.setLabel(label);
	}

	public static AfterLabelStep setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		TabsItemStep tabsItemStep = new TabsItemStep();

		return tabsItemStep.setLabel(labelUnsafeSupplier);
	}

	public static class TabsItemStep
		implements ActiveStep, AfterActiveStep, AfterDisabledStep,
				   AfterHrefStep, AfterLabelStep, AfterPutDataStep,
				   AfterSetDataStep, BuildStep, DisabledStep, HrefStep,
				   LabelStep, PutDataStep, SetDataStep {

		@Override
		public TabsItem build() {
			return _tabsItem;
		}

		@Override
		public AfterPutDataStep putData(String key, String value) {
			_tabsItem.putData(key, value);

			return this;
		}

		@Override
		public AfterPutDataStep putData(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

			try {
				String value = valueUnsafeSupplier.get();

				if (value != null) {
					_tabsItem.putData(key, value);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterActiveStep setActive(boolean active) {
			_tabsItem.setActive(active);

			return this;
		}

		@Override
		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

			try {
				Boolean active = activeUnsafeSupplier.get();

				if (active != null) {
					_tabsItem.setActive(active.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterSetDataStep setData(Map<String, Object> data) {
			_tabsItem.setData(data);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(boolean disabled) {
			_tabsItem.setDisabled(disabled);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(
			UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

			try {
				Boolean disabled = disabledUnsafeSupplier.get();

				if (disabled != null) {
					_tabsItem.setDisabled(disabled.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterHrefStep setHref(Object href) {
			_tabsItem.setHref(href);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			PortletURL portletURL, Object... parameters) {

			_tabsItem.setHref(portletURL, parameters);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

			try {
				Object href = hrefUnsafeSupplier.get();

				if (href != null) {
					_tabsItem.setHref(href);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterLabelStep setLabel(String label) {
			_tabsItem.setLabel(label);

			return this;
		}

		@Override
		public AfterLabelStep setLabel(
			UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

			try {
				String label = labelUnsafeSupplier.get();

				if (label != null) {
					_tabsItem.setLabel(label);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private final TabsItem _tabsItem = new TabsItem();

	}

	public interface ActiveStep {

		public AfterActiveStep setActive(boolean active);

		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier);

	}

	public interface AfterActiveStep
		extends BuildStep, DisabledStep, HrefStep, LabelStep, SetDataStep {
	}

	public interface AfterDisabledStep extends BuildStep, HrefStep, LabelStep {
	}

	public interface AfterHrefStep extends BuildStep, LabelStep {
	}

	public interface AfterLabelStep extends BuildStep {
	}

	public interface AfterPutDataStep
		extends ActiveStep, BuildStep, DisabledStep, HrefStep, LabelStep,
				PutDataStep, SetDataStep {
	}

	public interface AfterSetDataStep
		extends BuildStep, DisabledStep, HrefStep, LabelStep {
	}

	public interface BuildStep {

		public TabsItem build();

	}

	public interface DisabledStep {

		public AfterDisabledStep setDisabled(boolean disabled);

		public AfterDisabledStep setDisabled(
			UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier);

	}

	public interface HrefStep {

		public AfterHrefStep setHref(Object href);

		public AfterHrefStep setHref(
			PortletURL portletURL, Object... parameters);

		public AfterHrefStep setHref(
			UnsafeSupplier<Object, Exception> hrefUnsafeSupplier);

	}

	public interface LabelStep {

		public AfterLabelStep setLabel(String label);

		public AfterLabelStep setLabel(
			UnsafeSupplier<String, Exception> labelUnsafeSupplier);

	}

	public interface PutDataStep {

		public AfterPutDataStep putData(String key, String value);

		public AfterPutDataStep putData(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier);

	}

	public interface SetDataStep {

		public AfterSetDataStep setData(Map<String, Object> data);

	}

}
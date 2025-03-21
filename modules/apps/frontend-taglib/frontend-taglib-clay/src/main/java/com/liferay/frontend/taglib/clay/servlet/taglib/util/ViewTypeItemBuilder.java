/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import com.liferay.petra.function.UnsafeSupplier;

import jakarta.portlet.PortletURL;

import java.util.Map;

/**
 * @author Hugo Huijser
 */
public class ViewTypeItemBuilder {

	public static AfterPutDataStep putData(String key, String value) {
		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.putData(key, value);
	}

	public static AfterPutDataStep putData(
		String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.putData(key, valueUnsafeSupplier);
	}

	public static AfterActiveStep setActive(boolean active) {
		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setActive(active);
	}

	public static AfterActiveStep setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setActive(activeUnsafeSupplier);
	}

	public static AfterSetDataStep setData(Map<String, Object> data) {
		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setData(data);
	}

	public static AfterDisabledStep setDisabled(boolean disabled) {
		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setDisabled(disabled);
	}

	public static AfterDisabledStep setDisabled(
		UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setDisabled(disabledUnsafeSupplier);
	}

	public static AfterHrefStep setHref(Object href) {
		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setHref(href);
	}

	public static AfterHrefStep setHref(
		PortletURL portletURL, Object... parameters) {

		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setHref(parameters);
	}

	public static AfterHrefStep setHref(
		UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setHref(hrefUnsafeSupplier);
	}

	public static AfterIconStep setIcon(String icon) {
		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setIcon(icon);
	}

	public static AfterIconStep setIcon(
		UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setIcon(iconUnsafeSupplier);
	}

	public static AfterLabelStep setLabel(String label) {
		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setLabel(label);
	}

	public static AfterLabelStep setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		ViewTypeItemStep viewTypeItemStep = new ViewTypeItemStep();

		return viewTypeItemStep.setLabel(labelUnsafeSupplier);
	}

	public static class ViewTypeItemStep
		implements ActiveStep, AfterActiveStep, AfterDisabledStep,
				   AfterHrefStep, AfterIconStep, AfterLabelStep,
				   AfterPutDataStep, AfterSetDataStep, BuildStep, DisabledStep,
				   HrefStep, IconStep, LabelStep, PutDataStep, SetDataStep {

		@Override
		public ViewTypeItem build() {
			return _viewTypeItem;
		}

		@Override
		public AfterPutDataStep putData(String key, String value) {
			_viewTypeItem.putData(key, value);

			return this;
		}

		@Override
		public AfterPutDataStep putData(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

			try {
				String value = valueUnsafeSupplier.get();

				if (value != null) {
					_viewTypeItem.putData(key, value);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterActiveStep setActive(boolean active) {
			_viewTypeItem.setActive(active);

			return this;
		}

		@Override
		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

			try {
				Boolean active = activeUnsafeSupplier.get();

				if (active != null) {
					_viewTypeItem.setActive(active.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterSetDataStep setData(Map<String, Object> data) {
			_viewTypeItem.setData(data);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(boolean disabled) {
			_viewTypeItem.setDisabled(disabled);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(
			UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

			try {
				Boolean disabled = disabledUnsafeSupplier.get();

				if (disabled != null) {
					_viewTypeItem.setDisabled(disabled.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterHrefStep setHref(Object href) {
			_viewTypeItem.setHref(href);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			PortletURL portletURL, Object... parameters) {

			_viewTypeItem.setHref(portletURL, parameters);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

			try {
				Object href = hrefUnsafeSupplier.get();

				if (href != null) {
					_viewTypeItem.setHref(href);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterIconStep setIcon(String icon) {
			_viewTypeItem.setIcon(icon);

			return this;
		}

		@Override
		public AfterIconStep setIcon(
			UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

			try {
				String icon = iconUnsafeSupplier.get();

				if (icon != null) {
					_viewTypeItem.setIcon(icon);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterLabelStep setLabel(String label) {
			_viewTypeItem.setLabel(label);

			return this;
		}

		@Override
		public AfterLabelStep setLabel(
			UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

			try {
				String label = labelUnsafeSupplier.get();

				if (label != null) {
					_viewTypeItem.setLabel(label);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private final ViewTypeItem _viewTypeItem = new ViewTypeItem();

	}

	public interface ActiveStep {

		public AfterActiveStep setActive(boolean active);

		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier);

	}

	public interface AfterActiveStep
		extends BuildStep, DisabledStep, HrefStep, IconStep, LabelStep,
				SetDataStep {
	}

	public interface AfterDisabledStep
		extends BuildStep, HrefStep, IconStep, LabelStep {
	}

	public interface AfterHrefStep extends BuildStep, IconStep, LabelStep {
	}

	public interface AfterIconStep extends BuildStep, LabelStep {
	}

	public interface AfterLabelStep extends BuildStep {
	}

	public interface AfterPutDataStep
		extends ActiveStep, BuildStep, DisabledStep, HrefStep, IconStep,
				LabelStep, PutDataStep, SetDataStep {
	}

	public interface AfterSetDataStep
		extends BuildStep, DisabledStep, HrefStep, IconStep, LabelStep {
	}

	public interface BuildStep {

		public ViewTypeItem build();

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

	public interface IconStep {

		public AfterIconStep setIcon(String icon);

		public AfterIconStep setIcon(
			UnsafeSupplier<String, Exception> iconUnsafeSupplier);

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
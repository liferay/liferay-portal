/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import com.liferay.petra.function.UnsafeSupplier;

import jakarta.portlet.PortletURL;

import java.util.List;
import java.util.Map;

/**
 * @author Hugo Huijser
 */
public class DropdownItemBuilder {

	public static AfterPutDataStep putData(String key, String value) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.putData(key, value);
	}

	public static AfterPutDataStep putData(
		String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.putData(key, valueUnsafeSupplier);
	}

	public static AfterActiveStep setActive(boolean active) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setActive(active);
	}

	public static AfterActiveStep setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setActive(activeUnsafeSupplier);
	}

	public static AfterSetDataStep setData(Map<String, Object> data) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setData(data);
	}

	public static AfterDisabledStep setDisabled(boolean disabled) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setDisabled(disabled);
	}

	public static AfterDisabledStep setDisabled(
		UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setDisabled(disabledUnsafeSupplier);
	}

	public static AfterDropdownItemsStep setDropdownItems(
		List<DropdownItem> dropdownItems) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setDropdownItems(dropdownItems);
	}

	public static AfterHrefStep setHref(Object href) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setHref(href);
	}

	public static AfterHrefStep setHref(
		PortletURL portletURL, Object... parameters) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setHref(portletURL, parameters);
	}

	public static AfterHrefStep setHref(
		UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setHref(hrefUnsafeSupplier);
	}

	public static AfterIconStep setIcon(String icon) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setIcon(icon);
	}

	public static AfterIconStep setIcon(
		UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setIcon(iconUnsafeSupplier);
	}

	public static AfterLabelStep setLabel(String label) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setLabel(label);
	}

	public static AfterLabelStep setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setLabel(labelUnsafeSupplier);
	}

	public static AfterQuickActionStep setQuickAction(boolean quickAction) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setQuickAction(quickAction);
	}

	public static AfterQuickActionStep setQuickAction(
		UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setQuickAction(quickActionUnsafeSupplier);
	}

	public static AfterSeparatorStep setSeparator(boolean separator) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setSeparator(separator);
	}

	public static AfterSeparatorStep setSeparator(
		UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setSeparator(separatorUnsafeSupplier);
	}

	public static AfterTargetStep setTarget(String target) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setTarget(target);
	}

	public static AfterTargetStep setTarget(
		UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setTarget(targetUnsafeSupplier);
	}

	public static AfterTypeStep setType(String type) {
		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setType(type);
	}

	public static AfterTypeStep setType(
		UnsafeSupplier<String, Exception> typeUnsafeSupplier) {

		DropdownItemStep dropdownItemStep = new DropdownItemStep();

		return dropdownItemStep.setType(typeUnsafeSupplier);
	}

	public static class DropdownItemStep
		implements ActiveStep, AfterActiveStep, AfterDisabledStep,
				   AfterDropdownItemsStep, AfterHrefStep, AfterIconStep,
				   AfterKeyStep, AfterLabelStep, AfterPutDataStep,
				   AfterQuickActionStep, AfterSeparatorStep, AfterSetDataStep,
				   AfterTargetStep, AfterTypeStep, BuildStep, DisabledStep,
				   DropdownItemsStep, HrefStep, IconStep, LabelStep,
				   PutDataStep, QuickActionStep, SeparatorStep, SetDataStep,
				   TargetStep, TypeStep {

		@Override
		public DropdownItem build() {
			return _dropdownItem;
		}

		@Override
		public AfterPutDataStep putData(String key, String value) {
			_dropdownItem.putData(key, value);

			return this;
		}

		@Override
		public AfterPutDataStep putData(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

			try {
				String value = valueUnsafeSupplier.get();

				if (value != null) {
					_dropdownItem.putData(key, value);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterActiveStep setActive(boolean active) {
			_dropdownItem.setActive(active);

			return this;
		}

		@Override
		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

			try {
				Boolean active = activeUnsafeSupplier.get();

				if (active != null) {
					_dropdownItem.setActive(active.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterSetDataStep setData(Map<String, Object> data) {
			_dropdownItem.setData(data);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(boolean disabled) {
			_dropdownItem.setDisabled(disabled);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(
			UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

			try {
				Boolean disabled = disabledUnsafeSupplier.get();

				if (disabled != null) {
					_dropdownItem.setDisabled(disabled.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterDropdownItemsStep setDropdownItems(
			List<DropdownItem> dropdownItems) {

			_dropdownItem.setDropdownItems(dropdownItems);

			return this;
		}

		@Override
		public AfterHrefStep setHref(Object href) {
			_dropdownItem.setHref(href);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			PortletURL portletURL, Object... parameters) {

			_dropdownItem.setHref(portletURL, parameters);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

			try {
				Object href = hrefUnsafeSupplier.get();

				if (href != null) {
					_dropdownItem.setHref(href);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterIconStep setIcon(String icon) {
			_dropdownItem.setIcon(icon);

			return this;
		}

		@Override
		public AfterIconStep setIcon(
			UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

			try {
				String icon = iconUnsafeSupplier.get();

				if (icon != null) {
					_dropdownItem.setIcon(icon);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterKeyStep setKey(String key) {
			_dropdownItem.setKey(key);

			return this;
		}

		@Override
		public AfterKeyStep setKey(
			UnsafeSupplier<String, Exception> keyUnsafeSupplier) {

			try {
				String key = keyUnsafeSupplier.get();

				if (key != null) {
					_dropdownItem.setKey(key);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterLabelStep setLabel(String label) {
			_dropdownItem.setLabel(label);

			return this;
		}

		@Override
		public AfterLabelStep setLabel(
			UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

			try {
				String label = labelUnsafeSupplier.get();

				if (label != null) {
					_dropdownItem.setLabel(label);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterQuickActionStep setQuickAction(boolean quickAction) {
			_dropdownItem.setQuickAction(quickAction);

			return this;
		}

		@Override
		public AfterQuickActionStep setQuickAction(
			UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier) {

			try {
				Boolean quickAction = quickActionUnsafeSupplier.get();

				if (quickAction != null) {
					_dropdownItem.setQuickAction(quickAction.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterSeparatorStep setSeparator(boolean separator) {
			_dropdownItem.setSeparator(separator);

			return this;
		}

		@Override
		public AfterSeparatorStep setSeparator(
			UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier) {

			try {
				Boolean separator = separatorUnsafeSupplier.get();

				if (separator != null) {
					_dropdownItem.setSeparator(separator.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterTargetStep setTarget(String target) {
			_dropdownItem.setTarget(target);

			return this;
		}

		@Override
		public AfterTargetStep setTarget(
			UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

			try {
				String target = targetUnsafeSupplier.get();

				if (target != null) {
					_dropdownItem.setTarget(target);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterTypeStep setType(String type) {
			_dropdownItem.setType(type);

			return this;
		}

		@Override
		public AfterTypeStep setType(
			UnsafeSupplier<String, Exception> typeUnsafeSupplier) {

			try {
				String type = typeUnsafeSupplier.get();

				if (type != null) {
					_dropdownItem.setType(type);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private final DropdownItem _dropdownItem = new DropdownItem();

	}

	public interface ActiveStep {

		public AfterActiveStep setActive(boolean active);

		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier);

	}

	public interface AfterActiveStep
		extends BuildStep, DisabledStep, DropdownItemsStep, HrefStep, IconStep,
				LabelStep, QuickActionStep, SeparatorStep, SetDataStep,
				TargetStep, TypeStep {
	}

	public interface AfterDisabledStep
		extends BuildStep, DropdownItemsStep, HrefStep, IconStep, LabelStep,
				QuickActionStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterDropdownItemsStep
		extends BuildStep, IconStep, KeyStep, LabelStep, QuickActionStep,
				SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterHrefStep
		extends BuildStep, DropdownItemsStep, IconStep, KeyStep, LabelStep,
				QuickActionStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterIconStep
		extends BuildStep, DropdownItemsStep, KeyStep, LabelStep,
				QuickActionStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterKeyStep
		extends BuildStep, LabelStep, QuickActionStep, SeparatorStep,
				TargetStep, TypeStep {
	}

	public interface AfterLabelStep
		extends BuildStep, QuickActionStep, SeparatorStep, TargetStep,
				TypeStep {
	}

	public interface AfterPutDataStep
		extends ActiveStep, BuildStep, DisabledStep, DropdownItemsStep,
				HrefStep, IconStep, KeyStep, LabelStep, PutDataStep,
				QuickActionStep, SeparatorStep, SetDataStep, TargetStep,
				TypeStep {
	}

	public interface AfterQuickActionStep
		extends BuildStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterSeparatorStep
		extends BuildStep, TargetStep, TypeStep {
	}

	public interface AfterSetDataStep
		extends BuildStep, DisabledStep, DropdownItemsStep, HrefStep, IconStep,
				LabelStep, QuickActionStep, SeparatorStep, TargetStep,
				TypeStep {
	}

	public interface AfterTargetStep extends BuildStep, TypeStep {
	}

	public interface AfterTypeStep extends BuildStep {
	}

	public interface BuildStep {

		public DropdownItem build();

	}

	public interface DisabledStep {

		public AfterDisabledStep setDisabled(boolean disabled);

		public AfterDisabledStep setDisabled(
			UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier);

	}

	public interface DropdownItemsStep {

		public AfterDropdownItemsStep setDropdownItems(
			List<DropdownItem> dropdownItems);

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

	public interface KeyStep {

		public AfterKeyStep setKey(String key);

		public AfterKeyStep setKey(
			UnsafeSupplier<String, Exception> keyUnsafeSupplier);

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

	public interface QuickActionStep {

		public AfterQuickActionStep setQuickAction(boolean quickAction);

		public AfterQuickActionStep setQuickAction(
			UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier);

	}

	public interface SeparatorStep {

		public AfterSeparatorStep setSeparator(boolean separator);

		public AfterSeparatorStep setSeparator(
			UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier);

	}

	public interface SetDataStep {

		public AfterSetDataStep setData(Map<String, Object> data);

	}

	public interface TargetStep {

		public AfterTargetStep setTarget(String target);

		public AfterTargetStep setTarget(
			UnsafeSupplier<String, Exception> targetUnsafeSupplier);

	}

	public interface TypeStep {

		public AfterTypeStep setType(String type);

		public AfterTypeStep setType(
			UnsafeSupplier<String, Exception> typeUnsafeSupplier);

	}

}
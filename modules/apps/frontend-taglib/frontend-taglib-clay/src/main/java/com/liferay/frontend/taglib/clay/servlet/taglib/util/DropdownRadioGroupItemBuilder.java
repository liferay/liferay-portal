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
public class DropdownRadioGroupItemBuilder {

	public static AfterPutDataStep putData(String key, String value) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.putData(key, value);
	}

	public static AfterPutDataStep putData(
		String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.putData(key, valueUnsafeSupplier);
	}

	public static AfterActiveStep setActive(boolean active) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setActive(active);
	}

	public static AfterActiveStep setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setActive(activeUnsafeSupplier);
	}

	public static AfterSetDataStep setData(Map<String, Object> data) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setData(data);
	}

	public static AfterDisabledStep setDisabled(boolean disabled) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setDisabled(disabled);
	}

	public static AfterDisabledStep setDisabled(
		UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setDisabled(disabledUnsafeSupplier);
	}

	public static AfterDropdownItemsStep setDropdownItems(
		List<DropdownItem> dropdownItems) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setDropdownItems(dropdownItems);
	}

	public static AfterHrefStep setHref(Object href) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setHref(href);
	}

	public static AfterHrefStep setHref(
		PortletURL portletURL, Object... parameters) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setHref(portletURL, parameters);
	}

	public static AfterHrefStep setHref(
		UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setHref(hrefUnsafeSupplier);
	}

	public static AfterIconStep setIcon(String icon) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setIcon(icon);
	}

	public static AfterIconStep setIcon(
		UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setIcon(iconUnsafeSupplier);
	}

	public static AfterInputNameStep setInputName(String inputName) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setInputName(inputName);
	}

	public static AfterInputNameStep setInputName(
		UnsafeSupplier<String, Exception> inputNameUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setInputName(inputNameUnsafeSupplier);
	}

	public static AfterLabelStep setLabel(String label) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setLabel(label);
	}

	public static AfterLabelStep setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setLabel(labelUnsafeSupplier);
	}

	public static AfterQuickActionStep setQuickAction(boolean quickAction) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setQuickAction(quickAction);
	}

	public static AfterQuickActionStep setQuickAction(
		UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setQuickAction(
			quickActionUnsafeSupplier);
	}

	public static AfterSeparatorStep setSeparator(boolean separator) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setSeparator(separator);
	}

	public static AfterSeparatorStep setSeparator(
		UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setSeparator(separatorUnsafeSupplier);
	}

	public static AfterTargetStep setTarget(String target) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setTarget(target);
	}

	public static AfterTargetStep setTarget(
		UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setTarget(targetUnsafeSupplier);
	}

	public static AfterTypeStep setType(String type) {
		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setType(type);
	}

	public static AfterTypeStep setType(
		UnsafeSupplier<String, Exception> typeUnsafeSupplier) {

		DropdownRadioGroupItemStep dropdownRadioGroupItemStep =
			new DropdownRadioGroupItemStep();

		return dropdownRadioGroupItemStep.setType(typeUnsafeSupplier);
	}

	public static class DropdownRadioGroupItemStep
		implements ActiveStep, AfterActiveStep, AfterDisabledStep,
				   AfterDropdownItemsStep, AfterHrefStep, AfterIconStep,
				   AfterInputNameStep, AfterLabelStep, AfterPutDataStep,
				   AfterQuickActionStep, AfterSeparatorStep, AfterSetDataStep,
				   AfterTargetStep, AfterTypeStep, BuildStep, DisabledStep,
				   DropdownItemsStep, HrefStep, IconStep, InputNameStep,
				   LabelStep, PutDataStep, QuickActionStep, SeparatorStep,
				   SetDataStep, TargetStep, TypeStep {

		@Override
		public DropdownRadioGroupItem build() {
			return _dropdownRadioGroupItem;
		}

		@Override
		public AfterPutDataStep putData(String key, String value) {
			_dropdownRadioGroupItem.putData(key, value);

			return this;
		}

		@Override
		public AfterPutDataStep putData(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

			try {
				String value = valueUnsafeSupplier.get();

				if (value != null) {
					_dropdownRadioGroupItem.putData(key, value);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterActiveStep setActive(boolean active) {
			_dropdownRadioGroupItem.setActive(active);

			return this;
		}

		@Override
		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

			try {
				Boolean active = activeUnsafeSupplier.get();

				if (active != null) {
					_dropdownRadioGroupItem.setActive(active.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterSetDataStep setData(Map<String, Object> data) {
			_dropdownRadioGroupItem.setData(data);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(boolean disabled) {
			_dropdownRadioGroupItem.setDisabled(disabled);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(
			UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

			try {
				Boolean disabled = disabledUnsafeSupplier.get();

				if (disabled != null) {
					_dropdownRadioGroupItem.setDisabled(
						disabled.booleanValue());
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

			_dropdownRadioGroupItem.setDropdownItems(dropdownItems);

			return this;
		}

		@Override
		public AfterHrefStep setHref(Object href) {
			_dropdownRadioGroupItem.setHref(href);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			PortletURL portletURL, Object... parameters) {

			_dropdownRadioGroupItem.setHref(portletURL, parameters);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

			try {
				Object href = hrefUnsafeSupplier.get();

				if (href != null) {
					_dropdownRadioGroupItem.setHref(href);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterIconStep setIcon(String icon) {
			_dropdownRadioGroupItem.setIcon(icon);

			return this;
		}

		@Override
		public AfterIconStep setIcon(
			UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

			try {
				String icon = iconUnsafeSupplier.get();

				if (icon != null) {
					_dropdownRadioGroupItem.setIcon(icon);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterInputNameStep setInputName(String inputName) {
			_dropdownRadioGroupItem.setInputName(inputName);

			return this;
		}

		@Override
		public AfterInputNameStep setInputName(
			UnsafeSupplier<String, Exception> inputNameUnsafeSupplier) {

			try {
				String inputName = inputNameUnsafeSupplier.get();

				if (inputName != null) {
					_dropdownRadioGroupItem.setInputName(inputName);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterLabelStep setLabel(String label) {
			_dropdownRadioGroupItem.setLabel(label);

			return this;
		}

		@Override
		public AfterLabelStep setLabel(
			UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

			try {
				String label = labelUnsafeSupplier.get();

				if (label != null) {
					_dropdownRadioGroupItem.setLabel(label);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterQuickActionStep setQuickAction(boolean quickAction) {
			_dropdownRadioGroupItem.setQuickAction(quickAction);

			return this;
		}

		@Override
		public AfterQuickActionStep setQuickAction(
			UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier) {

			try {
				Boolean quickAction = quickActionUnsafeSupplier.get();

				if (quickAction != null) {
					_dropdownRadioGroupItem.setQuickAction(
						quickAction.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterSeparatorStep setSeparator(boolean separator) {
			_dropdownRadioGroupItem.setSeparator(separator);

			return this;
		}

		@Override
		public AfterSeparatorStep setSeparator(
			UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier) {

			try {
				Boolean separator = separatorUnsafeSupplier.get();

				if (separator != null) {
					_dropdownRadioGroupItem.setSeparator(
						separator.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterTargetStep setTarget(String target) {
			_dropdownRadioGroupItem.setTarget(target);

			return this;
		}

		@Override
		public AfterTargetStep setTarget(
			UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

			try {
				String target = targetUnsafeSupplier.get();

				if (target != null) {
					_dropdownRadioGroupItem.setTarget(target);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterTypeStep setType(String type) {
			_dropdownRadioGroupItem.setType(type);

			return this;
		}

		@Override
		public AfterTypeStep setType(
			UnsafeSupplier<String, Exception> typeUnsafeSupplier) {

			try {
				String type = typeUnsafeSupplier.get();

				if (type != null) {
					_dropdownRadioGroupItem.setType(type);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private final DropdownRadioGroupItem _dropdownRadioGroupItem =
			new DropdownRadioGroupItem();

	}

	public interface ActiveStep {

		public AfterActiveStep setActive(boolean active);

		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier);

	}

	public interface AfterActiveStep
		extends BuildStep, DisabledStep, DropdownItemsStep, HrefStep, IconStep,
				InputNameStep, LabelStep, QuickActionStep, SeparatorStep,
				SetDataStep, TargetStep, TypeStep {
	}

	public interface AfterDisabledStep
		extends BuildStep, DropdownItemsStep, HrefStep, IconStep, InputNameStep,
				LabelStep, QuickActionStep, SeparatorStep, TargetStep,
				TypeStep {
	}

	public interface AfterDropdownItemsStep
		extends BuildStep, HrefStep, IconStep, InputNameStep, LabelStep,
				QuickActionStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterHrefStep
		extends BuildStep, IconStep, InputNameStep, LabelStep, QuickActionStep,
				SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterIconStep
		extends BuildStep, InputNameStep, LabelStep, QuickActionStep,
				SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterInputNameStep
		extends BuildStep, LabelStep, QuickActionStep, SeparatorStep,
				TargetStep, TypeStep {
	}

	public interface AfterLabelStep
		extends BuildStep, QuickActionStep, SeparatorStep, TargetStep,
				TypeStep {
	}

	public interface AfterPutDataStep
		extends ActiveStep, BuildStep, DisabledStep, HrefStep, IconStep,
				LabelStep, PutDataStep, QuickActionStep, SeparatorStep,
				SetDataStep, TargetStep, TypeStep {
	}

	public interface AfterQuickActionStep
		extends BuildStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterSeparatorStep
		extends BuildStep, TargetStep, TypeStep {
	}

	public interface AfterSetDataStep
		extends BuildStep, DisabledStep, HrefStep, IconStep, LabelStep,
				QuickActionStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterTargetStep extends BuildStep, TypeStep {
	}

	public interface AfterTypeStep extends BuildStep {
	}

	public interface BuildStep {

		public DropdownRadioGroupItem build();

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

	public interface InputNameStep {

		public AfterInputNameStep setInputName(String inputName);

		public AfterInputNameStep setInputName(
			UnsafeSupplier<String, Exception> inputNameUnsafeSupplier);

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
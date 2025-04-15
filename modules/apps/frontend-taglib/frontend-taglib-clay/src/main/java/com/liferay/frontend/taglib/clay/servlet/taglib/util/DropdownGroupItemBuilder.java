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
public class DropdownGroupItemBuilder {

	public static AfterPutDataStep putData(String key, String value) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.putData(key, value);
	}

	public static AfterPutDataStep putData(
		String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.putData(key, valueUnsafeSupplier);
	}

	public static AfterActiveStep setActive(boolean active) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setActive(active);
	}

	public static AfterActiveStep setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setActive(activeUnsafeSupplier);
	}

	public static AfterSetDataStep setData(Map<String, Object> data) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setData(data);
	}

	public static AfterDisabledStep setDisabled(boolean disabled) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setDisabled(disabled);
	}

	public static AfterDisabledStep setDisabled(
		UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setDisabled(disabledUnsafeSupplier);
	}

	public static AfterDropdownItemsStep setDropdownItems(
		List<DropdownItem> dropdownItems) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setDropdownItems(dropdownItems);
	}

	public static AfterHrefStep setHref(Object href) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setHref(href);
	}

	public static AfterHrefStep setHref(
		PortletURL portletURL, Object... parameters) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setHref(portletURL, parameters);
	}

	public static AfterHrefStep setHref(
		UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setHref(hrefUnsafeSupplier);
	}

	public static AfterIconStep setIcon(String icon) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setIcon(icon);
	}

	public static AfterIconStep setIcon(
		UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setIcon(iconUnsafeSupplier);
	}

	public static AfterLabelStep setLabel(String label) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setLabel(label);
	}

	public static AfterLabelStep setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setLabel(labelUnsafeSupplier);
	}

	public static AfterQuickActionStep setQuickAction(boolean quickAction) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setQuickAction(quickAction);
	}

	public static AfterQuickActionStep setQuickAction(
		UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setQuickAction(quickActionUnsafeSupplier);
	}

	public static AfterSeparatorStep setSeparator(boolean separator) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setSeparator(separator);
	}

	public static AfterSeparatorStep setSeparator(
		UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setSeparator(separatorUnsafeSupplier);
	}

	public static AfterTargetStep setTarget(String target) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setTarget(target);
	}

	public static AfterTargetStep setTarget(
		UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setTarget(targetUnsafeSupplier);
	}

	public static AfterTypeStep setType(String type) {
		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setType(type);
	}

	public static AfterTypeStep setType(
		UnsafeSupplier<String, Exception> typeUnsafeSupplier) {

		DropdownGroupItemStep dropdownGroupItemStep =
			new DropdownGroupItemStep();

		return dropdownGroupItemStep.setType(typeUnsafeSupplier);
	}

	public static class DropdownGroupItemStep
		implements ActiveStep, AfterActiveStep, AfterDisabledStep,
				   AfterDropdownItemsStep, AfterHrefStep, AfterIconStep,
				   AfterLabelStep, AfterPutDataStep, AfterQuickActionStep,
				   AfterSeparatorStep, AfterSetDataStep, AfterTargetStep,
				   AfterTypeStep, BuildStep, DisabledStep, DropdownItemsStep,
				   HrefStep, IconStep, LabelStep, PutDataStep, QuickActionStep,
				   SeparatorStep, SetDataStep, TargetStep, TypeStep {

		@Override
		public DropdownGroupItem build() {
			return _dropdownGroupItem;
		}

		@Override
		public AfterPutDataStep putData(String key, String value) {
			_dropdownGroupItem.putData(key, value);

			return this;
		}

		@Override
		public AfterPutDataStep putData(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

			try {
				String value = valueUnsafeSupplier.get();

				if (value != null) {
					_dropdownGroupItem.putData(key, value);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterActiveStep setActive(boolean active) {
			_dropdownGroupItem.setActive(active);

			return this;
		}

		@Override
		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

			try {
				Boolean active = activeUnsafeSupplier.get();

				if (active != null) {
					_dropdownGroupItem.setActive(active.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterSetDataStep setData(Map<String, Object> data) {
			_dropdownGroupItem.setData(data);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(boolean disabled) {
			_dropdownGroupItem.setDisabled(disabled);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(
			UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

			try {
				Boolean disabled = disabledUnsafeSupplier.get();

				if (disabled != null) {
					_dropdownGroupItem.setDisabled(disabled.booleanValue());
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

			_dropdownGroupItem.setDropdownItems(dropdownItems);

			return this;
		}

		@Override
		public AfterHrefStep setHref(Object href) {
			_dropdownGroupItem.setHref(href);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			PortletURL portletURL, Object... parameters) {

			_dropdownGroupItem.setHref(portletURL, parameters);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

			try {
				Object href = hrefUnsafeSupplier.get();

				if (href != null) {
					_dropdownGroupItem.setHref(href);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterIconStep setIcon(String icon) {
			_dropdownGroupItem.setIcon(icon);

			return this;
		}

		@Override
		public AfterIconStep setIcon(
			UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

			try {
				String icon = iconUnsafeSupplier.get();

				if (icon != null) {
					_dropdownGroupItem.setIcon(icon);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterLabelStep setLabel(String label) {
			_dropdownGroupItem.setLabel(label);

			return this;
		}

		@Override
		public AfterLabelStep setLabel(
			UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

			try {
				String label = labelUnsafeSupplier.get();

				if (label != null) {
					_dropdownGroupItem.setLabel(label);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterQuickActionStep setQuickAction(boolean quickAction) {
			_dropdownGroupItem.setQuickAction(quickAction);

			return this;
		}

		@Override
		public AfterQuickActionStep setQuickAction(
			UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier) {

			try {
				Boolean quickAction = quickActionUnsafeSupplier.get();

				if (quickAction != null) {
					_dropdownGroupItem.setQuickAction(
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
			_dropdownGroupItem.setSeparator(separator);

			return this;
		}

		@Override
		public AfterSeparatorStep setSeparator(
			UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier) {

			try {
				Boolean separator = separatorUnsafeSupplier.get();

				if (separator != null) {
					_dropdownGroupItem.setSeparator(separator.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterTargetStep setTarget(String target) {
			_dropdownGroupItem.setTarget(target);

			return this;
		}

		@Override
		public AfterTargetStep setTarget(
			UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

			try {
				String target = targetUnsafeSupplier.get();

				if (target != null) {
					_dropdownGroupItem.setTarget(target);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterTypeStep setType(String type) {
			_dropdownGroupItem.setType(type);

			return this;
		}

		@Override
		public AfterTypeStep setType(
			UnsafeSupplier<String, Exception> typeUnsafeSupplier) {

			try {
				String type = typeUnsafeSupplier.get();

				if (type != null) {
					_dropdownGroupItem.setType(type);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private final DropdownGroupItem _dropdownGroupItem =
			new DropdownGroupItem();

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
		extends BuildStep, HrefStep, IconStep, LabelStep, QuickActionStep,
				SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterHrefStep
		extends BuildStep, IconStep, LabelStep, QuickActionStep, SeparatorStep,
				TargetStep, TypeStep {
	}

	public interface AfterIconStep
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

		public DropdownGroupItem build();

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
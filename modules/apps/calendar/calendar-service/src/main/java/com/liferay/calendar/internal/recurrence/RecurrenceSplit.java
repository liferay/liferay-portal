/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.internal.recurrence;

import com.liferay.calendar.recurrence.Recurrence;

/**
 * Represents the result of splitting a <code>Recurrence</code> (in the
 * <code>com.liferay.calendar.api</code> module) object.
 *
 * <p>
 * A <code>Recurrence</code> object is frequently split at a specific date. If
 * the recurrence starts before the split date and ends after the split date,
 * then there are two new recurrences: one going up to the split date and the
 * other starting at the split date and continuing to the original recurrence's
 * end.
 * </p>
 *
 * <p>
 * If the recurrence does not include the split date, however, only the original
 * recurrence is needed.
 * </p>
 *
 * <p>
 * This class represents the result of this operation. If the split
 * operation is successful (i.e. generated two new recurrences), their values
 * can be acquired through the methods {@link #getFirstRecurrence()} and {@link
 * #getSecondRecurrence()}. In this case, {@link #isSplit()} returns
 * <code>true</code>.
 * </p>
 *
 * <p>
 * If the split operation failed (i.e. the split date was outside the recurrence
 * range), the recurrence value can still be acquired via the {@link
 * #getFirstRecurrence()} method. This would be a new recurrence instance whose
 * values are identical to the original recurrence. In this case, {@link
 * #isSplit()} returns <code>false</code>.
 * </p>
 *
 * @author Adam Brandizzi
 */
public class RecurrenceSplit {

	public RecurrenceSplit(
		Recurrence firstRecurrence, Recurrence secondRecurrence) {

		_firstRecurrence = firstRecurrence;
		_secondRecurrence = secondRecurrence;
	}

	public Recurrence getFirstRecurrence() {
		return _firstRecurrence;
	}

	public Recurrence getSecondRecurrence() {
		return _secondRecurrence;
	}

	public boolean isSplit() {
		if (_secondRecurrence != null) {
			return true;
		}

		return false;
	}

	private final Recurrence _firstRecurrence;
	private final Recurrence _secondRecurrence;

}
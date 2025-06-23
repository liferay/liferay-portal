/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';

import BaseWrapper from './BaseWrapper';
import Checkbox from './Checkbox';
import DateRange from './DateRange';
import Input from './Input';
import BaseWarning from './InputWarning';
import MultiSelect from './MultiSelect';
import Renderer from './Renderer';
import Select from './Select';

const Form = () => {};

Form.BaseWarning = BaseWarning;
Form.BaseWrapper = BaseWrapper;
Form.Clay = ClayForm;
Form.Checkbox = Checkbox;
Form.DateRange = DateRange;
Form.Divider = (props: React.HTMLAttributes<HTMLHRElement>) => (
	<hr {...props} />
);
Form.Input = Input;
Form.MultiSelect = MultiSelect;
Form.Select = Select;
Form.Renderer = Renderer;

export default Form;

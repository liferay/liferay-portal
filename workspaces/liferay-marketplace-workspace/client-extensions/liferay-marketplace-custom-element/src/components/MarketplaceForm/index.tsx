/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormHTMLAttributes, ReactNode} from 'react';
import {FormProvider} from 'react-hook-form';

import Checkbox from './Checkbox';
import FormControl from './FormControl';
import {HelpMessage} from './HelpMessage';
import {Input} from './Input';
import {Label} from './Label';
import RichTextEditor from './RichText';
import {SectionWithControllers} from './SectionWithControllers';

function Divider(props: React.HTMLAttributes<HTMLHRElement>) {
	<hr {...props} />;
}

type FormProps = {
	children: ReactNode;
	formProviderProps: any;
} & FormHTMLAttributes<HTMLFormElement>;

type FormChildrens = {
	Checkbox: typeof Checkbox;
	Divider: typeof Divider;
	FormControl: typeof FormControl;
	HelpMessage: typeof HelpMessage;
	Input: typeof Input;
	Label: typeof Label;
	RichTextEditor: typeof RichTextEditor;
	SectionWithControllers: typeof SectionWithControllers;
};

const Form: React.FC<FormProps> & FormChildrens = ({
	children,
	formProviderProps,
	...formProps
}) => (
	<FormProvider {...formProviderProps}>
		<form className="my-3 space-y-5" {...formProps}>
			{children}
		</form>
	</FormProvider>
);

Form.Checkbox = Checkbox;
Form.Divider = Divider;
Form.FormControl = FormControl;
Form.HelpMessage = HelpMessage;
Form.Input = Input;
Form.Label = Label;
Form.RichTextEditor = RichTextEditor;
Form.SectionWithControllers = SectionWithControllers;

export default Form;

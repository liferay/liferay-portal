import BaseToolbar from 'shared/components/toolbar/BaseToolbar';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import React from 'react';
import {Link} from 'react-router-dom';

interface IToolbarProps {
	backURL: string;
	children?: React.ReactNode;
	className?: string;
	title: string;
}

const Item = BaseToolbar.Item;

const Divider: React.FC = () => (
	<BaseToolbar.Item
		className="align-self-stretch border-left my-1 p-0"
		data-testid="toolbar-divider"
	/>
);

const Cancel: React.FC<{href: string}> = ({href}) => (
	<BaseToolbar.Item>
		<ClayLink
			borderless
			button
			className="rounded-lg"
			displayType="secondary"
			href={href}
			small
		>
			{Liferay.Language.get('cancel')}
		</ClayLink>
	</BaseToolbar.Item>
);

interface ISaveProps {
	disabled?: boolean;
	form?: string;
	label: string;
	onClick?: () => void;
	type?: 'button' | 'submit';
}

const Save: React.FC<ISaveProps> = ({
	disabled = false,
	form,
	label,
	onClick,
	type = 'button',
}) => (
	<BaseToolbar.Item>
		<ClayButton
			className="rounded-lg"
			disabled={disabled}
			displayType="primary"
			form={form}
			onClick={onClick}
			size="sm"
			type={type}
		>
			{label}
		</ClayButton>
	</BaseToolbar.Item>
);

const Toolbar: React.FC<IToolbarProps> & {
	Cancel: typeof Cancel;
	Divider: typeof Divider;
	Item: typeof Item;
	Save: typeof Save;
} = ({backURL, children, className, title}) => (
	<BaseToolbar className={className}>
		<BaseToolbar.Item>
			<Link
				aria-label={Liferay.Language.get('back')}
				className="btn btn-monospaced btn-outline-borderless btn-outline-secondary btn-sm rounded-lg"
				data-tooltip-align="bottom"
				title={Liferay.Language.get('back')}
				to={backURL}
			>
				<ClayIcon symbol="angle-left" />
			</Link>
		</BaseToolbar.Item>

		<BaseToolbar.Item className="pl-0">
			<BaseToolbar.Section>
				<h1 className="font-weight-semi-bold m-0 text-5 text-dark text-nowrap">
					{title}
				</h1>
			</BaseToolbar.Section>
		</BaseToolbar.Item>

		<BaseToolbar.Item expand />

		{children}
	</BaseToolbar>
);

Toolbar.Cancel = Cancel;
Toolbar.Divider = Divider;
Toolbar.Item = Item;
Toolbar.Save = Save;

export default Toolbar;

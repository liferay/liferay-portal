import BaseToolbar from 'shared/components/toolbar/BaseToolbar';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import getCN from 'classnames';
import React from 'react';
import {Link} from 'react-router-dom';

interface IToolbarProps {
	backURL: string;
	children?: React.ReactNode;
	className?: string;
	title: string;
}

const Item = BaseToolbar.Item;

/**
 * Hidden below 992px, together with Cancel.
 */
const Divider: React.FC = () => (
	<BaseToolbar.Item
		className="align-self-stretch border-left d-lg-flex d-none my-1 p-0"
		data-testid="toolbar-divider"
	/>
);

/**
 * Hidden below 992px, where the back link already leads back to the list.
 */
const Cancel: React.FC<{href: string}> = ({href}) => (
	<BaseToolbar.Item className="d-lg-flex d-none">
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

/**
 * Page header with a back link, the title, and the actions passed as children.
 * The actions stay right-aligned and move below the title, wrapping 24px
 * apart, when they run out of room, so do not give the header a fixed height.
 */
const Toolbar: React.FC<IToolbarProps> & {
	Cancel: typeof Cancel;
	Divider: typeof Divider;
	Item: typeof Item;
	Save: typeof Save;
} = ({backURL, children, className, title}) => (
	<BaseToolbar className={getCN('base-edit-page-toolbar', className)}>
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

		<BaseToolbar.Item className="ml-auto">
			<ul
				className="base-edit-page-toolbar-actions tbar-nav"
				data-testid="toolbar-actions"
			>
				{children}
			</ul>
		</BaseToolbar.Item>
	</BaseToolbar>
);

Toolbar.Cancel = Cancel;
Toolbar.Divider = Divider;
Toolbar.Item = Item;
Toolbar.Save = Save;

export default Toolbar;

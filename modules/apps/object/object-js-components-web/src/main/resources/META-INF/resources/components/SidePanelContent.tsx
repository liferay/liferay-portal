/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayLabel from '@clayui/label';
import classNames from 'classnames';
import React from 'react';

import './SidePanelContent.scss';

export function closeSidePanel() {
	const parentWindow = Liferay.Util.getOpener();
	parentWindow.Liferay.fire('close-side-panel');
}

export function saveAndReload() {
	const parentWindow = Liferay.Util.getOpener();

	closeSidePanel();

	setTimeout(() => {
		parentWindow.location.reload();
	}, 300);
}

export function openToast(options: {
	autoClose?: boolean,
	message: string;
	type?: 'danger' | 'info' | 'success';
}) {
	const parentWindow = Liferay.Util.getOpener();
	parentWindow.Liferay.Util.openToast(options);
}

export function SidePanelContent({
	children,
	className,
	customLabel,
	onSave,
	readOnly,
	title,
}: IProps) {
	const saveProps: {
		onClick?: () => void;
		type?: 'submit';
	} = onSave ? {onClick: onSave} : {type: 'submit'};

	return (
		<div
			className={classNames('lfr-objects__side-panel-content', className)}
		>
			<div className="lfr-objects__side-panel-content-header">
				<div className="lfr-objects__side-panel-content-header-title">
					<h3 className="mb-0">{title}</h3>

					{customLabel && (
						<ClayLabel
							className="lfr-objects__side-panel-content-header-title-label"
							displayType={customLabel?.displayType}
						>
							{customLabel?.message}
						</ClayLabel>
					)}
				</div>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('cancel')}
					displayType="unstyled"
					monospaced={false}
					onClick={closeSidePanel}
					symbol="times"
				/>
			</div>

			{children}

			<ClayButton.Group
				className="lfr-objects__side-panel-content-container"
				spaced
			>
				<ClayButton displayType="secondary" onClick={closeSidePanel}>
					{Liferay.Language.get('cancel')}
				</ClayButton>

				<ClayButton disabled={readOnly} {...saveProps}>
					{Liferay.Language.get('save')}
				</ClayButton>
			</ClayButton.Group>
		</div>
	);
}

export function SidePanelForm({
	children,
	customLabel,
	onSubmit,
	readOnly,
	title,
}: ISidePanelFormProps) {
	return (
		<ClayForm onSubmit={onSubmit}>
			<SidePanelContent
				customLabel={customLabel}
				readOnly={readOnly}
				title={title}
			>
				{children}
			</SidePanelContent>
		</ClayForm>
	);
}

interface IContainerProps {
	children: React.ReactNode;
	className?: string;
}

interface CommonProps extends IContainerProps {
	customLabel?: {
		displayType: 'success' | 'info';
		message: string;
	};
	readOnly?: boolean;
	title: string;
}

interface IProps extends CommonProps {
	onSave?: () => void;
}

interface ISidePanelFormProps extends CommonProps {
	onSubmit?: React.FormEventHandler<HTMLFormElement>;
}

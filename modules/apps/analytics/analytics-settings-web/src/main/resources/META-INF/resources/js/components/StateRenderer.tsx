/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React, {createContext, useContext} from 'react';

import {ERROR_MESSAGE} from '../utils/constants';
import Loading from './Loading';

const Context = createContext<IStateRendererProps>({
	empty: false,
	error: false,
	loading: false,
});

interface IStateRendererProps extends React.HTMLAttributes<HTMLElement> {
	empty: boolean;
	error: boolean;
	loading: boolean;
	loadingProps?: any;
}

const StateRenderer: React.FC<
	{children?: React.ReactNode | undefined} & IStateRendererProps
> & {
	Empty: typeof EmptyState;
	Error: typeof ErrorState;
	Success: typeof SuccessState;
} = ({children, empty, error, loading, loadingProps}) => {
	if (loading) {
		return <Loading {...loadingProps} />;
	}

	return (
		<Context.Provider value={{empty, error, loading}}>
			{children}
		</Context.Provider>
	);
};

const EmptyState: React.FC<
	{children?: React.ReactNode | undefined} & React.HTMLAttributes<HTMLElement>
> = ({children}) => {
	const {empty, error, loading} = useContext(Context);

	return !error && !loading && empty ? <>{children}</> : null;
};

const ErrorState: React.FC<
	{children?: React.ReactNode | undefined} & React.HTMLAttributes<HTMLElement>
> = ({children}) => {
	const {error, loading} = useContext(Context);

	return !loading && error ? <>{children}</> : null;
};

const SuccessState: React.FC<
	{children?: React.ReactNode | undefined} & React.HTMLAttributes<HTMLElement>
> = ({children}) => {
	const {empty, error, loading} = useContext(Context);

	return !error && !loading && !empty ? <>{children}</> : null;
};

interface IErrorStateComponentProps extends React.HTMLAttributes<HTMLElement> {
	disabled?: boolean;
	onClickRefetch?: () => void;
}

export function ErrorStateComponent({
	className,
	disabled,
	onClickRefetch,
}: IErrorStateComponentProps) {
	return (
		<div className={className}>
			<ClayEmptyState
				className="text-center"
				description=""
				title={ERROR_MESSAGE}
			>
				{onClickRefetch && (
					<ClayButton
						disabled={disabled}
						displayType="secondary"
						onClick={onClickRefetch}
					>
						{Liferay.Language.get('try-again')}
					</ClayButton>
				)}
			</ClayEmptyState>
		</div>
	);
}

interface IEmptyStateComponentProps extends React.HTMLAttributes<HTMLElement> {
	description?: string;
	symbol?: string;
	title?: string;
}

export function EmptyStateComponent({
	children,
	className,
	description,
	symbol,
	title,
}: IEmptyStateComponentProps) {
	return (
		<div className={className}>
			<ClayEmptyState
				description={description}
				image={
					symbol ? (
						<ClayIcon
							className={`c-empty-state-icon c-empty-state-icon-${symbol}`}
							illustration
							symbol={symbol}
						/>
					) : undefined
				}
				title={title}
			>
				{children}
			</ClayEmptyState>
		</div>
	);
}

StateRenderer.Empty = EmptyState;
StateRenderer.Error = ErrorState;
StateRenderer.Success = SuccessState;

export default StateRenderer;

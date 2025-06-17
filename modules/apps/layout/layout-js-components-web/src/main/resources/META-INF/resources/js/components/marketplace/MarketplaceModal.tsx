/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {
	AppsPermissions,
	Marketplace,
	MarketplaceContext,
	MarketplaceContextProvider,
	MarketplaceRest,
	MarketplaceView,
	useMarketplaceContext,
} from '@liferay/marketplace-js-components-web';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {
	ComponentProps,
	ReactElement,
	ReactNode,
	cloneElement,
	useCallback,
	useEffect,
	useState,
} from 'react';

import MarketplaceViews from './MarketplaceViews';

interface MarketplaceModalProps {
	children?: ReactNode;
	className?: string;
	openOnRender?: boolean;
	permissions: AppsPermissions & {
		manageFragmentsEntries?: boolean;
	};
	portletNamespace: string;
	trigger?: ReactElement | null;
}

export default function MarketplaceModal({
	children,
	className,
	openOnRender,
	permissions,
	portletNamespace,
	trigger,
	...marketplaceViewProps
}: MarketplaceModalProps & ComponentProps<typeof MarketplaceViews>) {
	const [title, setTitle] = useState<string | undefined>();

	return (

		// @ts-ignore

		<MarketplaceContextProvider
			baseResourceURL={MarketplaceRest.getBaseResourceURL(
				portletNamespace
			)}
			permissions={permissions}
			settings={{productFilter: 'fragments'}}
		>
			{children}

			<MarketplaceContext.Consumer>
				{({view}) => (
					<Marketplace.Modal
						noConnectionMessage={Liferay.Language.get(
							'please-go-to-instance-settings-to-enable-the-connection'
						)}
						size={
							view === MarketplaceView.PURCHASE
								? ('md' as any)
								: 'full-screen'
						}
						title={title}
						trigger={
							<MarketplaceModalTrigger
								className={className}
								openOnRender={openOnRender}
								setTitle={setTitle}
								trigger={trigger}
							/>
						}
					>
						<MarketplaceViews {...marketplaceViewProps} />
					</Marketplace.Modal>
				)}
			</MarketplaceContext.Consumer>
		</MarketplaceContextProvider>
	);
}

interface MarketplaceModalTriggerProps {
	className?: string;
	openOnRender?: boolean;
	setTitle: React.Dispatch<React.SetStateAction<string | undefined>>;
	trigger?: ReactElement | null;
}

function MarketplaceModalTrigger({
	className,
	openOnRender,
	setTitle,
	trigger,
}: MarketplaceModalTriggerProps) {
	const {
		modal: {onOpenChange},
		product,
		setView,
		view,
	} = useMarketplaceContext();

	const handleClick = useCallback(() => {
		if (view === MarketplaceView.PURCHASE) {
			setView(MarketplaceView.PRODUCTS);
		}

		onOpenChange(true);
	}, [view, setView, onOpenChange]);

	useEffect(() => {
		setTitle(
			view === MarketplaceView.PURCHASE && product
				? sub(Liferay.Language.get('installing-x'), product.name)
				: undefined
		);
	}, [view, product, setTitle]);

	useEffect(() => {
		if (openOnRender) {
			onOpenChange(true);
		}
	}, [onOpenChange, openOnRender]);

	if (trigger === null) {
		return null;
	}

	if (trigger) {
		return cloneElement(trigger, {
			onClick: (event: React.MouseEvent) => {
				if (trigger.props.onClick) {
					trigger.props.onClick(event);
				}
				else {
					handleClick();
				}
			},
		});
	}

	return (
		<ClayButtonWithIcon
			aria-label={Liferay.Language.get('open-marketplace-explorer')}
			borderless
			className={classNames(className, 'marketplace-modal-trigger')}
			displayType="secondary"
			monospaced
			onClick={handleClick}
			size="sm"
			symbol="marketplace"
			title={Liferay.Language.get('open-marketplace-explorer')}
		/>
	);
}

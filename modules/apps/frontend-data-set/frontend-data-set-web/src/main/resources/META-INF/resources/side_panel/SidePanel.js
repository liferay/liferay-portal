/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ReactPortal} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React from 'react';

import Modal from '../modal/Modal';
import debounce from '../utils/debounce';
import {
	CLOSE_SIDE_PANEL,
	IFRAME_LOADED,
	OPEN_SIDE_PANEL,
	SIDE_PANEL_CLOSED,
} from '../utils/eventsDefinitions';
import {
	isPageInIframe,
	subscribeModal,
	unsubscribeModal,
} from '../utils/iframes';
import {logError} from '../utils/logError';
import {exposeSidePanel} from '../utils/sidePanels';
import SideMenu from './SideMenu';

const SIDE_PANEL_TITLE_HEIGHT = 65;
export default class SidePanel extends React.Component {
	static defaultSize = 'md';

	constructor(props) {
		super(props);
		this.state = {
			active: null,
			closeButtonStyle: null,
			currentURL: props.url || null,
			disableHeader: props.disableHeader || true,
			iframeHandlerModalId: subscribeModal(),
			loading: true,
			menuCoverTopDistance: 0,
			moving: false,
			onAfterSubmit: props.onAfterSubmit || null,
			size: props.size || this.defaultSize,
			title: props.title || undefined,
			topDistance: 0,
			visible: !!props.visible,
			wrapper:
				document.querySelector(this.props.wrapperSelector) ||
				document.querySelector('body'),
		};
		this.handleIframeClickOnSubmit =
			this.handleIframeClickOnSubmit.bind(this);
		this.handleIframeSubmit = this.handleIframeSubmit.bind(this);
		this.handleContentLoaded = this.handleContentLoaded.bind(this);
		this.close = this.close.bind(this);
		this.open = this.open.bind(this);
		this.handlePanelOpenEvent = this.handlePanelOpenEvent.bind(this);
		this.handlePanelCloseEvent = this.handlePanelCloseEvent.bind(this);
		this.handleKeyupEvent = this.handleKeyupEvent.bind(this);
		this.updateTop = this.updateTop.bind(this);
		this.debouncedUpdateTop = debounce(this.updateTop, 250);
		this.panel = React.createRef();
		this.iframeRef = React.createRef();
	}

	componentDidMount() {
		this._isMounted = true;

		if (this.props.topAnchorSelector) {
			window.addEventListener('resize', this.debouncedUpdateTop);
			this.updateTop();
		}

		if (this.props.containerSelector) {
			const container = document.querySelector(
				this.props.containerSelector
			);
			if (container) {
				container.classList.add('with-side-panel');
			}
			else {
				logError(
					`Container: "${this.props.containerSelector}" not found!`
				);
			}
		}

		Liferay.on(OPEN_SIDE_PANEL, this.handlePanelOpenEvent);
		Liferay.on(CLOSE_SIDE_PANEL, this.handlePanelCloseEvent);

		exposeSidePanel(this.props.id, () => ({
			activeMenuItem: this.state.active,
			disableHeader: this.state.disableHeader,
			size: this.state.size,
			title: this.state.title,
			url: this.state.currentURL,
			visible: this.state.visible,
		}));
	}

	handlePanelOpenEvent(event) {
		if (event.id !== this.props.id) {
			return this.close();
		}

		this.open(event.url, event.slug);

		this.setState({
			disableHeader:
				event.disableHeader !== undefined
					? event.disableHeader
					: this.state.disableHeader,
			onAfterSubmit: event.onSubmit || null,
			size: event.size || this.defaultSize,
			title: event.title,
		});
	}

	handlePanelCloseEvent(event) {
		event.preventDefault();

		return this.close();
	}

	handleKeyupEvent(even) {
		const eventDocument = even.view?.document ?? even.target?.ownerDocument;

		if (
			even.keyCode !== 27 ||
			window.top.document.querySelector('.modal-content') ||
			eventDocument?.querySelector('.modal-content')
		) {
			return;
		}

		if (this.iframeRef.current?.contentWindow) {
			const nestedIframe =
				this.iframeRef.current.contentDocument.querySelector(
					'.side-panel iframe'
				);

			if (
				!nestedIframe ||
				nestedIframe.contentDocument.location.origin === 'null'
			) {
				this.close();
			}
		}
	}

	componentWillUnmount() {
		unsubscribeModal(this.state.iframeHandlerModalId);
		this._isMounted = false;
		window.removeEventListener('keyup', this.handleKeyupEvent);

		if (this.props.topAnchorSelector) {
			window.removeEventListener('resize', this.debouncedUpdateTop);
		}

		Liferay.detach(OPEN_SIDE_PANEL, this.handlePanelOpenEvent);
	}

	updateTop() {
		if (!this._isMounted) {
			return;
		}

		const topAnchor = document.querySelector(this.props.topAnchorSelector);

		if (topAnchor) {
			const {height, top} = topAnchor.getBoundingClientRect();
			this.setState({
				topDistance: top + height + 'px',
			});
		}

		const pageHeader = document.querySelector('.page-header');

		if (pageHeader) {
			const {top} = pageHeader.getBoundingClientRect();

			this.setState({
				menuCoverTopDistance: top + 'px',
			});
		}
		else if (isPageInIframe()) {
			this.setState({
				menuCoverTopDistance: SIDE_PANEL_TITLE_HEIGHT + 'px',
			});
		}
	}

	load(url, refreshPageAfterSubmit) {
		if (!this._isMounted) {
			return;
		}

		this.setState(
			{
				currentURL: url,
				loading: true,
				onAfterSubmit: refreshPageAfterSubmit
					? () => window.location.reload()
					: null,
			},
			() => {
				if (this.iframeRef.current?.contentWindow) {
					this.iframeRef.current.contentWindow.location =
						this.state.currentURL;
				}
			}
		);
	}

	open(url = this.state.currentURL, active = null) {
		this.setState({active, closeButtonStyle: null});

		if (!this.state.visible) {
			this.toggle(true).then(() => {
				this.load(url);
			});
		}
		else if (url !== this.state.currentURL) {
			this.load(url);
		}
	}

	close() {
		this.toggle(false).then(() => {
			if (!this._isMounted) {
				return;
			}

			this.setState({
				active: null,
				closeButtonStyle: null,
				currentURL: null,
				loading: true,
			});
			Liferay.fire(SIDE_PANEL_CLOSED, {
				id: this.props.id,
			});
		});
	}

	toggle(visible = !this.state.visible) {
		if (visible) {
			window.addEventListener('keyup', this.handleKeyupEvent);
		}
		else {
			window.removeEventListener('keyup', this.handleKeyupEvent);
			window.focus();
		}

		return new Promise((resolve) => {
			this.setState({moving: true, visible});

			if (!this.panel.current) {
				return;
			}

			this.panel.current.addEventListener(
				'transitionend',
				() => {
					if (this._isMounted) {
						this.setState({moving: false}, () => resolve(status));
					}
				},
				{
					once: true,
				}
			);
		});
	}

	handleIframeSubmit(even) {
		if (even.id !== this.props.id) {
			return;
		}

		Liferay.detach(IFRAME_LOADED, this.handleIframeSubmit);

		if (this.props.onAfterSubmit) {
			this.props.onAfterSubmit();
		}
	}

	handleIframeClickOnSubmit() {
		Liferay.on(IFRAME_LOADED, this.handleIframeSubmit);

		setTimeout(() => {
			Liferay.detach(IFRAME_LOADED, this.handleIframeSubmit);
		}, 3000);
	}

	handleContentLoaded() {
		Liferay.fire(IFRAME_LOADED, {
			id: this.props.id,
		});

		this.setState({
			loading: false,
		});

		try {
			const iframeDocument = this.iframeRef.current.contentDocument;
			const iframeWindow = this.iframeRef.current.contentWindow;

			iframeWindow.addEventListener('keyup', (event) =>
				this.handleKeyupEvent(event)
			);

			if (iframeWindow.Liferay) {
				iframeWindow.Liferay.on('endNavigate', () => {
					this.handleIframeSubmit({id: this.props.id});
					iframeWindow.addEventListener('keyup', (event) =>
						this.handleKeyupEvent(event)
					);
				});
			}

			const submitButton =
				iframeDocument.querySelector('[type="submit"]');

			if (submitButton) {
				submitButton.addEventListener(
					'click',
					this.handleIframeClickOnSubmit
				);
			}
		}
		catch (error) {
			logError(
				`Cannot access to iframe body. URL: "${this.state.currentURL}"`
			);
		}
	}

	render() {
		const loading =
			this.state.loading || (this.state.moving && this.state.visible)
				? 'is-loading'
				: '';
		const moving = this.state.moving ? 'is-moving' : '';
		const visibility = this.state.visible ? 'is-visible' : 'is-hidden';

		const content = (
			<>
				<Modal id={this.state.iframeHandlerModalId} />
				<div
					className={classNames(
						'fds-side-panel-nav-cover navigation-bar border-bottom',
						visibility
					)}
					style={{top: this.state.menuCoverTopDistance}}
				>
					<div
						className={classNames(!isPageInIframe() && 'container')}
					>
						<ul className="navbar-nav">
							<li className="nav-item">
								<button
									aria-label={Liferay.Language.get('back')}
									className="btn btn-unstyled nav-link"
									onClick={() => this.close()}
									title={Liferay.Language.get('back')}
								>
									<ClayIcon symbol="angle-left" />
								</button>
							</li>
						</ul>
					</div>
				</div>
				<div
					className={classNames(
						'fds-side-panel',
						`fds-side-panel-${this.state.size}`,
						moving,
						visibility,
						loading
					)}
					ref={this.panel}
					style={{top: this.state.topDistance}}
				>
					{this.props.items?.length && (
						<SideMenu
							active={this.state.active}
							items={this.props.items}
							open={this.open}
						/>
					)}

					{!this.state.disableHeader && (
						<div className="fds-side-panel-header">
							<div className="fds-side-panel-title">
								<h3 className="mb-0">{this.state.title}</h3>
							</div>

							<ClayButton
								aria-label={Liferay.Language.get('close')}
								className={classNames(
									'fds-side-panel-close',
									this.state.closeButtonStyle === 'simple' &&
										'fds-side-panel-close-simple',
									this.state.closeButtonStyle === 'menu' &&
										'fds-side-panel-close-menu'
								)}
								displayType="monospaced"
								onClick={() => this.close()}
								title={Liferay.Language.get('close')}
							>
								<ClayIcon symbol="times" />
							</ClayButton>
						</div>
					)}

					<div className="tab-content">
						<div className="loader">
							<ClayLoadingIndicator />
						</div>

						<div
							className="active fade show tab-pane"
							role="tabpanel"
						>
							{!(this.state.moving && this.state.visible) && (
								<iframe
									frameBorder="0"
									onLoad={this.handleContentLoaded}
									ref={this.iframeRef}
									src={this.state.currentURL}
								></iframe>
							)}
						</div>
					</div>
				</div>
			</>
		);

		return (
			<ReactPortal container={this.state.wrapper}>{content}</ReactPortal>
		);
	}
}

SidePanel.propTypes = {
	id: PropTypes.string,
	items: PropTypes.any,
	size: PropTypes.oneOf(['xs', 'sm', 'md', 'lg', 'xl', 'full']),
	topAnchorSelector: PropTypes.any,
	wrapperSelector: PropTypes.string,
};

SidePanel.defaultProps = {
	size: 'lg',
	topAnchorSelector: '.control-menu',
	wrapperSelector: '.side-panel-wrapper',
};

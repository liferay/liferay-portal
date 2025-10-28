/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent} from '@testing-library/dom';
import userEvent from '@testing-library/user-event';
import {EventEmitter, buildFragment} from 'frontend-js-web';

import App from '../../src/main/resources/META-INF/resources/app/App';
import Route from '../../src/main/resources/META-INF/resources/route/Route';
import HtmlScreen from '../../src/main/resources/META-INF/resources/screen/HtmlScreen';
import Screen from '../../src/main/resources/META-INF/resources/screen/Screen';
import Surface from '../../src/main/resources/META-INF/resources/surface/Surface';
import {
	getCurrentBrowserPath,
	getNodeOffset,
	getUrlPathWithoutHash,
} from '../../src/main/resources/META-INF/resources/util/utils';
class StubScreen extends Screen {}
StubScreen.prototype.activate = jest.fn();
StubScreen.prototype.beforeDeactivate = jest.fn();
StubScreen.prototype.deactivate = jest.fn();
StubScreen.prototype.disposeInternal = jest.fn();
StubScreen.prototype.evaluateScripts = jest.fn();
StubScreen.prototype.evaluateStyles = jest.fn();
StubScreen.prototype.flip = jest.fn();
StubScreen.prototype.load = jest.fn(() => Promise.resolve());
StubScreen.prototype.preloadStyles = jest.fn();

describe('App', () => {
	let app = null;

	beforeAll(() => {
		window.Liferay.DOMTaskRunner = {
			runTasks: jest.fn(),
		};
	});

	beforeEach(() => {
		Liferay.SPA = {};

		const beforeunload = jest.fn();
		window.onbeforeunload = beforeunload;

		jest.resetAllMocks();

		jest.spyOn(window, 'scrollTo').mockImplementation((top, left) => {
			window.history.state.scrollTop = top;
			window.history.state.scrollLeft = left;
			window.pageXOffset = top;
			window.pageYOffset = left;
		});
	});

	afterEach(() => {
		if (app && !app.isDisposed()) {
			app.dispose();
		}
		app = null;
	});

	it('adds route', () => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		const route = app.findRoute('/path');
		expect(app.hasRoutes()).toBe(true);
		expect(route).toBeInstanceOf(Route);
		expect(route.getPath()).toBe('/path');
		expect(route.getHandler()).toBe(Screen);
	});

	it('removes route', () => {
		app = new App();
		const route = new Route('/path', Screen);
		app.addRoutes(route);
		expect(app.removeRoute(route)).toBe(true);
	});

	it('sadds route from object', () => {
		app = new App();
		app.addRoutes({
			handler: Screen,
			path: '/path',
		});
		const route = app.findRoute('/path');
		expect(app.hasRoutes()).toBe(true);
		expect(route).toBeInstanceOf(Route);
		expect(route.getPath()).toBe('/path');
		expect(route.getHandler()).toBe(Screen);
	});

	it('adds route from array', () => {
		app = new App();
		app.addRoutes([
			{
				handler: Screen,
				path: '/path',
			},
			new Route('/pathOther', Screen),
		]);
		const route = app.findRoute('/path');
		expect(app.hasRoutes()).toBe(true);
		expect(route).toBeInstanceOf(Route);
		expect(route.getPath()).toBe('/path');
		expect(route.getHandler()).toBe(Screen);
		const routeOther = app.findRoute('/pathOther');
		expect(routeOther).toBeInstanceOf(Route);
		expect(routeOther.getPath()).toBe('/pathOther');
		expect(routeOther.getHandler()).toBe(Screen);
	});

	it('does not find route for not registered paths', () => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		expect(app.findRoute('/pathOther')).toBeNull();
	});

	it('does not allow navigation for urls with hashbang when navigating to same basepath', () => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));

		const restoreWindowLocation = mockWindowLocation({
			hash: '',
			host: '',
			hostname: '',
			pathname: '/path',
			search: '',
		});

		expect(app.canNavigate('/path#hashbang')).toBe(false);

		restoreWindowLocation();
	});

	it('allows navigation for urls with hashbang when navigating to different basepath', () => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));

		const restoreWindowLocation = mockWindowLocation({
			hash: '',
			host: 'localhost:8080',
			hostname: 'localhost',
			origin: 'http://localhost:8080',
			pathname: '/path1',
			port: '8080',
			search: '',
		});

		expect(app.canNavigate('/path#hashbang')).toBe(true);

		restoreWindowLocation();
	});

	it('finds route for urls with hashbang for different basepath', () => {
		app = new App();
		app.addRoutes(new Route('/pathOther', Screen));

		const restoreWindowLocation = mockWindowLocation({
			host: 'localhost:8080',
			hostname: 'localhost',
			origin: 'http://localhost:8080',
			pathname: '/path',
			search: '',
		});

		expect(app.findRoute('/pathOther#hashbang')).toBeTruthy();

		restoreWindowLocation();
	});

	it('finds route for urls ending with or without slash', () => {
		app = new App();
		app.addRoutes(new Route('/pathOther', Screen));

		const restoreWindowLocation = mockWindowLocation({
			host: 'localhost:8080',
			hostname: 'localhost',
			origin: 'http://localhost:8080',
			pathname: '/path/',
			search: '',
		});

		expect(app.findRoute('/pathOther')).toBeTruthy();
		expect(app.findRoute('/pathOther/')).toBeTruthy();

		restoreWindowLocation();
	});

	it('ignores query string on findRoute when ignoreQueryStringFromRoutePath is enabled', () => {
		app = new App();
		app.setIgnoreQueryStringFromRoutePath(true);
		app.addRoutes(new Route('/path', Screen));
		expect(app.findRoute('/path?foo=1')).toBeTruthy();
	});

	it('does not ignore query string on findRoute when ignoreQueryStringFromRoutePath is disabled', () => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		expect(app.findRoute('/path?foo=1')).toBeFalsy();
	});

	it('adds surface', () => {
		app = new App();
		app.addSurfaces(new Surface('surfaceId'));
		expect(app.surfaces.surfaceId).toBeTruthy();
		expect(app.surfaces.surfaceId.getId()).toBe('surfaceId');
	});

	it('adds surface from surface id', () => {
		app = new App();
		app.addSurfaces('surfaceId');
		expect(app.surfaces.surfaceId).toBeTruthy();
		expect(app.surfaces.surfaceId.getId()).toBe('surfaceId');
	});

	it('adds surface from array', () => {
		app = new App();
		app.addSurfaces([new Surface('surfaceId'), 'surfaceIdOther']);
		expect(app.surfaces.surfaceId).toBeTruthy();
		expect(app.surfaces.surfaceIdOther).toBeTruthy();
		expect(app.surfaces.surfaceId.getId()).toBe('surfaceId');
		expect(app.surfaces.surfaceIdOther.getId()).toBe('surfaceIdOther');
	});

	it('creates screen instance to a route', () => {
		app = new App();
		const screen = app.createScreenInstance(
			'/path',
			new Route('/path', Screen)
		);
		expect(screen).toBeInstanceOf(Screen);
	});

	it('creates screen instance to a route for Screen class child', () => {
		app = new App();
		const screen = app.createScreenInstance(
			'/path',
			new Route('/path', HtmlScreen)
		);
		expect(screen).toBeInstanceOf(HtmlScreen);
	});

	it('creates screen instance to a route with function handler', () => {
		app = new App();
		const stub = jest.fn();
		const route = new Route('/path', stub);
		const screen = app.createScreenInstance('/path', route);
		expect(stub).toHaveBeenCalledTimes(1);
		expect(stub).toHaveBeenCalledWith(route);
		expect(stub).toHaveReturnedWith(undefined);
		expect(screen).toBeInstanceOf(Screen);
	});

	it('gets same screen instance to a route', () => {
		app = new App();
		const route = new Route('/path', Screen);
		const screen = app.createScreenInstance('/path', route);
		app.screens['/path'] = screen;
		expect(app.createScreenInstance('/path', route)).toBe(screen);
	});

	it('uses same screen instance when simulating navigate refresh', () => {
		app = new App();
		const route = new Route('/path', HtmlScreen);
		const screen = app.createScreenInstance('/path', route);
		app.screens['/path'] = screen;
		app.activePath = '/path';
		app.activeScreen = screen;
		const screenRefresh = app.createScreenInstance('/path', route);
		expect(screenRefresh).toBe(screen);
	});

	it('stores screen for path with query string when ignoreQueryStringFromRoutePath is enabled', (done) => {
		class NoCacheScreen extends Screen {
			constructor() {
				super();
			}
		}

		app = new App();
		app.setIgnoreQueryStringFromRoutePath(true);
		app.addRoutes(new Route('/path', NoCacheScreen));

		app.navigate('/path?foo=1').then(() => {
			expect(app.screens['/path?foo=1']).toBeTruthy();
			done();
		});
	});

	it('creates different screen instance when navigate to same path with different query strings if ignoreQueryStringFromRoutePath is enabled', (done) => {
		class NoCacheScreen extends Screen {
			constructor() {
				super();
			}
		}

		app = new App();
		app.setIgnoreQueryStringFromRoutePath(true);

		app.addRoutes(new Route('/path1', NoCacheScreen));
		app.addRoutes(new Route('/path2', NoCacheScreen));

		app.navigate('/path1?foo=1').then(() => {
			const screenFirstNavigate = app.screens['/path1?foo=1'];
			app.navigate('/path2').then(() => {
				app.navigate('/path1?foo=2').then(() => {
					expect(app.screens['/path1?foo=2']).not.toBe(
						screenFirstNavigate
					);
					done();
				});
			});
		});
	});

	it('creates different screen instance navigate when not cacheable', (done) => {
		class NoCacheScreen extends Screen {
			constructor() {
				super();
				this.cacheable = false;
			}
		}
		app = new App();
		app.addRoutes(new Route('/path1', NoCacheScreen));
		app.addRoutes(new Route('/path2', NoCacheScreen));
		app.navigate('/path1').then(() => {
			const screenFirstNavigate = app.screens['/path1'];
			app.navigate('/path2').then(() => {
				app.navigate('/path1').then(() => {
					expect(app.screens['/path1']).not.toBe(screenFirstNavigate);
					done();
				});
			});
		});
	});

	it('uses same screen instance navigate when is cacheable', (done) => {
		class CacheScreen extends Screen {
			constructor() {
				super();
				this.cacheable = true;
			}
		}
		app = new App();
		app.addRoutes(new Route('/path1', CacheScreen));
		app.addRoutes(new Route('/path2', CacheScreen));
		app.navigate('/path1').then(() => {
			const screenFirstNavigate = app.screens['/path1'];
			app.navigate('/path2').then(() => {
				app.navigate('/path1').then(() => {
					expect(app.screens['/path1']).toBe(screenFirstNavigate);
					done();
				});
			});
		});
	});

	it('clears screen cache', () => {
		app = new App();
		app.screens['/path'] = app.createScreenInstance(
			'/path',
			new Route('/path', HtmlScreen)
		);
		app.clearScreensCache();
		expect(Object.keys(app.screens)).toHaveLength(0);
	});

	it('clears all screen caches on app dispose', () => {
		app = new App();
		const screen1 = app.createScreenInstance(
			'/path1',
			new Route('/path1', HtmlScreen)
		);
		const screen2 = app.createScreenInstance(
			'/path2',
			new Route('/path2', HtmlScreen)
		);
		app.activePath = '/path1';
		app.activeScreen = screen1;
		app.screens['/path1'] = screen1;
		app.screens['/path2'] = screen2;
		app.dispose();
		expect(Object.keys(app.screens)).toHaveLength(0);
	});

	it('clears screen cache and remove surfaces', () => {
		app = new App();
		const surface = new Surface('surfaceId');
		surface.remove = jest.fn();
		app.addSurfaces(surface);
		app.screens['/path'] = app.createScreenInstance(
			'/path',
			new Route('/path', HtmlScreen)
		);
		app.clearScreensCache();
		expect(surface.remove).toHaveBeenCalledTimes(1);
	});

	it('clears screen cache for activeScreen but not remove it', () => {
		app = new App();
		app.screens['/path'] = app.createScreenInstance(
			'/path',
			new Route('/path', HtmlScreen)
		);
		app.activePath = '/path';
		app.activeScreen = app.screens['/path'];
		app.clearScreensCache();
		expect(Object.keys(app.screens)).toHaveLength(1);
		expect(app.activeScreen.getCache()).toBeNull();
	});

	it('does not clear screen cache the being used in a pending navigation', (done) => {
		const event = new EventEmitter();
		class StubScreen extends Screen {
			constructor() {
				super();

				this.cacheable = true;
			}

			flip(surfaces) {
				const promises = super.flip(surfaces);
				event.emit('flip');

				return promises;
			}
		}

		const callback = () => {

			// app.clearScreensCache();

			expect(Object.keys(app.screens)).toHaveLength(1);

			// event.dispose();

			done();
		};

		const route = new Route('/path1', StubScreen);

		app = new App();
		app.addSurfaces(new Surface('surfaceId'));
		app.screens['/path1'] = app.createScreenInstance('/path1', route);
		app.addRoutes(route);
		app.navigate('/path1');
		event.on('flip', callback);
	});

	it('sets flag to prevent navigate', () => {
		app = new App();
		expect(app.getAllowPreventNavigate()).toBe(true);
		app.setAllowPreventNavigate(false);
		expect(app.getAllowPreventNavigate()).toBe(false);
	});

	it('gets default title', () => {
		document.title = 'default';
		app = new App();
		expect(app.getDefaultTitle()).toBe('default');
		app.setDefaultTitle('title');
		expect(app.getDefaultTitle()).toBe('title');
	});

	it('gets basepath', () => {
		app = new App();
		expect(app.getBasePath()).toBe('');
		app.setBasePath('/base');
		expect(app.getBasePath()).toBe('/base');
	});

	it('gets update scroll position', () => {
		app = new App();
		expect(app.getUpdateScrollPosition()).toBe(true);
		app.setUpdateScrollPosition(false);
		expect(app.getUpdateScrollPosition()).toBe(false);
	});

	it('gets loading css class', () => {
		app = new App();
		expect(app.getLoadingCssClass()).toBe('senna-loading');
		app.setLoadingCssClass('');
		expect(app.getLoadingCssClass()).toBe('');
	});

	it('gets form selector', () => {
		app = new App();
		expect(app.getFormSelector()).toBe(
			'form[enctype="multipart/form-data"]:not([data-senna-off])'
		);
		app.setFormSelector('');
		expect(app.getFormSelector()).toBe('');
	});

	it('gets link selector', () => {
		app = new App();
		expect(app.getLinkSelector()).toBe(
			'a:not([data-senna-off]):not([target="_blank"])'
		);
		app.setLinkSelector('');
		expect(app.getLinkSelector()).toBe('');
	});

	it('tests if can navigate to url', () => {
		app = new App();

		const restoreWindowLocation = mockWindowLocation({
			host: 'localhost',
			hostname: 'localhost',
			origin: 'http://localhost',
			pathname: '/base/path',
			search: '',
		});

		app.setBasePath('/base');
		app.addRoutes([new Route('/', Screen), new Route('/path', Screen)]);

		expect(app.canNavigate('http://localhost/base/')).toBe(true);
		expect(app.canNavigate('http://localhost/base')).toBe(true);
		expect(app.canNavigate('http://localhost/base/path')).toBe(true);
		expect(app.canNavigate('http://localhost/base/path#')).toBe(false);
		expect(app.canNavigate('http://localhost/base/path#foo')).toBe(false);
		expect(app.canNavigate('http://localhost/base/path1')).toBe(false);
		expect(app.canNavigate('http://localhost/path')).toBe(false);
		expect(app.canNavigate('http://external/path')).toBe(false);
		expect(app.canNavigate('tel:+0101010101')).toBe(false);
		expect(app.canNavigate('mailto:contact@sennajs.com')).toBe(false);

		restoreWindowLocation();
	});

	it('tests if can navigate to url with base path ending in "/"', () => {
		app = new App();

		const restoreWindowLocation = mockWindowLocation({
			host: 'localhost',
			hostname: 'localhost',
			origin: 'http://localhost',
			pathname: '/path',
			search: '',
		});

		app.setBasePath('/base/');
		app.addRoutes([new Route('/', Screen), new Route('/path', Screen)]);

		expect(app.canNavigate('http://localhost/base/')).toBe(true);
		expect(app.canNavigate('http://localhost/base')).toBe(true);
		expect(app.canNavigate('http://localhost/base/path')).toBe(true);
		expect(app.canNavigate('http://localhost/base/path1')).toBe(false);
		expect(app.canNavigate('http://localhost/path')).toBe(false);
		expect(app.canNavigate('http://external/path')).toBe(false);

		restoreWindowLocation();
	});

	it('is able to navigate to route that ends with "/"', () => {
		app = new App();

		const restoreWindowLocation = mockWindowLocation({
			host: 'localhost',
			hostname: 'localhost',
			origin: 'http://localhost',
			pathname: '/path',
			search: '',
		});

		app.addRoutes([
			new Route('/path/', Screen),
			new Route('/path/(\\d+)/', Screen),
		]);

		expect(app.canNavigate('http://localhost/path')).toBe(true);
		expect(app.canNavigate('http://localhost/path/')).toBe(true);
		expect(app.canNavigate('http://localhost/path/123')).toBe(true);
		expect(app.canNavigate('http://localhost/path/123/')).toBe(true);

		restoreWindowLocation();
	});

	it('detects a navigation to different port and refresh page', () => {
		app = new App();

		const restoreWindowLocation = mockWindowLocation({
			host: 'localhost:8080',
			pathname: '/path',
			search: '',
		});

		app.addRoutes([
			new Route('/path/', Screen),
			new Route('/path/(\\d+)/', Screen),
		]);

		expect(app.canNavigate('http://localhost:9080/path')).toBe(false);
		expect(app.canNavigate('http://localhost:9081/path/')).toBe(false);
		expect(app.canNavigate('http://localhost:9082/path/123')).toBe(false);
		expect(app.canNavigate('http://localhost:9083/path/123/')).toBe(false);

		restoreWindowLocation();
	});

	it('is able to navigate to a path using default protocol port', () => {
		app = new App();

		const restoreWindowLocation = mockWindowLocation({
			host: 'localhost',
			origin: 'http://localhost',
			pathname: '/path',
			search: '',
		});

		app.addRoutes([
			new Route('/path/', Screen),
			new Route('/path/(\\d+)/', Screen),
		]);

		expect(app.canNavigate('http://localhost:80/path')).toBe(true);
		expect(app.canNavigate('http://localhost:80/path/')).toBe(true);
		expect(app.canNavigate('http://localhost:80/path/123')).toBe(true);
		expect(app.canNavigate('http://localhost:80/path/123/')).toBe(true);

		restoreWindowLocation();
	});

	it('stores proper senna state after navigate', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.navigate('/path').then(() => {
			const state = window.history.state;
			expect(state.path).toBe('/path');
			expect(state.redirectPath).toBe('/path');
			expect(state.scrollLeft).toBe(0);
			expect(state.scrollTop).toBe(0);
			expect(state.form).toBe(false);
			expect(state.referrer).toBeTruthy();
			expect(state.senna).toBeTruthy();
			done();
		});
	});

	it('emits startNavigate and endNavigate custom event', (done) => {
		const startNavigateStub = jest.fn();
		const endNavigateStub = jest.fn();
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.on('startNavigate', startNavigateStub);
		app.on('endNavigate', endNavigateStub);
		app.navigate('/path').then(() => {
			expect(startNavigateStub).toHaveBeenCalledTimes(1);
			expect(startNavigateStub).toHaveBeenCalledWith({
				path: '/path',
				replaceHistory: false,
			});
			expect(endNavigateStub).toHaveBeenCalledTimes(1);
			expect(endNavigateStub).toHaveBeenCalledWith({
				path: '/path',
			});
			done();
		});
	});

	it('emits startNavigate and endNavigate custom event with replace history', (done) => {
		const startNavigateStub = jest.fn();
		const endNavigateStub = jest.fn();
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.on('startNavigate', startNavigateStub);
		app.on('endNavigate', endNavigateStub);
		app.navigate('/path', true).then(() => {
			expect(startNavigateStub).toHaveBeenCalledTimes(1);
			expect(startNavigateStub).toHaveBeenCalledWith({
				path: '/path',
				replaceHistory: true,
			});
			expect(endNavigateStub).toHaveBeenCalledTimes(1);
			expect(endNavigateStub).toHaveBeenCalledWith({
				path: '/path',
			});
			done();
		});
	});

	it('overwrites event.path on beforeNavigate custom event', (done) => {
		app = new App();
		app.addRoutes(new Route('/path1', Screen));
		app.on('beforeNavigate', (event) => {
			event.path = '/path1';
		});
		app.navigate('/path').then(() => {
			expect(window.location.pathname).toBe('/path1');
			done();
		});
	});

	it.skip('cancels navigate', (done) => {
		const stub = jest.fn();
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.on('endNavigate', (payload) => {
			expect(payload.error).toBeInstanceOf(Error);
			stub();
		});
		app.navigate('/path')
			.catch((reason) => {
				expect(reason).toBeInstanceOf(Error);
				expect(stub).toHaveBeenCalledTimes(1);
				done();
			})
			.cancel();
	});

	it('clears pendingNavigate after navigate', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.navigate('/path').then(() => {
			expect(app.pendingNavigate).toBeFalsy();
			done();
		});
		expect(app.pendingNavigate).toBeTruthy();
	});

	it('waits for pendingNavigate if navigate to same path', (done) => {
		class CacheScreen extends Screen {
			constructor() {
				super();
				this.cacheable = true;
			}
		}
		app = new App();
		app.addRoutes(new Route('/path', CacheScreen));
		app.navigate('/path').then(() => {
			const pendingNavigate1 = app.navigate('/path');
			const pendingNavigate2 = app.navigate('/path');
			expect(pendingNavigate1).toBeTruthy();
			expect(pendingNavigate2).toBeTruthy();
			expect(pendingNavigate1).toBe(pendingNavigate2);
			done();
		});
	});

	it('navigates back when clicking browser back button', (done) => {
		app = new App();
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));
		app.navigate('/path1')
			.then(() => app.navigate('/path2'))
			.then(() => {
				const activeScreen = app.activeScreen;
				expect(window.location.pathname).toBe('/path2');
				app.once('endNavigate', () => {
					expect(window.location.pathname).toBe('/path1');
					expect(app.activeScreen).not.toBe(activeScreen);
					done();
				});
				window.history.back();
			});
	});

	it('does not navigate back on a hash change', (done) => {
		app = new App();
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));
		app.navigate('/path1')
			.then(() => app.navigate('/path1#hash'))
			.then(() => {
				const startNavigate = jest.fn();
				app.on('startNavigate', startNavigate);
				window.addEventListener(
					'popstate',
					() => {
						expect(startNavigate).not.toHaveBeenCalled();
						done();
					},
					{once: true}
				);
				window.history.back();
			});
	});

	it('only calls beforeNavigate when waiting for pendingNavigate if navigate to same path', (done) => {
		class CacheScreen extends Screen {
			constructor() {
				super();
				this.cacheable = true;
			}
		}
		app = new App();
		app.addRoutes(new Route('/path', CacheScreen));
		app.navigate('/path').then(() => {
			app.navigate('/path');
			const beforeNavigate = jest.fn();
			const startNavigate = jest.fn();
			app.on('beforeNavigate', beforeNavigate);
			app.on('startNavigate', startNavigate);
			app.navigate('/path');
			expect(beforeNavigate).toHaveBeenCalled();
			expect(startNavigate).not.toHaveBeenCalled();
			done();
		});
	});

	it('does not wait for pendingNavigate if navigate to different path', (done) => {
		class CacheScreen extends Screen {
			constructor() {
				super();
				this.cacheable = true;
			}
		}
		app = new App();
		app.addRoutes(new Route('/path1', CacheScreen));
		app.addRoutes(new Route('/path2', CacheScreen));
		app.navigate('/path1')
			.then(() => app.navigate('/path2'))
			.then(() => {
				const pendingNavigate1 = app.navigate('/path1');
				const pendingNavigate2 = app.navigate('/path2');
				expect(pendingNavigate1).toBeTruthy();
				expect(pendingNavigate2).toBeTruthy();
				expect(pendingNavigate1).not.toBe(pendingNavigate2);
				done();
			});
	});

	it('simulates refresh on navigate to the same path', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.once('startNavigate', (payload) => {
			expect(payload.replaceHistory).toBeFalsy();
		});
		app.navigate('/path').then(() => {
			app.once('startNavigate', (payload) => {
				expect(payload.replaceHistory).toBeTruthy();
			});
			app.navigate('/path').then(() => {
				done();
			});
		});
	});

	it('adds loading css class on navigate', (done) => {
		const containsLoadingCssClass = () => {
			return document.documentElement.classList.contains(
				app.getLoadingCssClass()
			);
		};
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.on('startNavigate', () =>
			expect(containsLoadingCssClass()).toBe(true)
		);
		app.on('endNavigate', () => {
			expect(containsLoadingCssClass()).toBe(false);
			done();
		});
		app.navigate('/path').then(() =>
			expect(containsLoadingCssClass()).toBe(false)
		);
	});

	it.skip('does not remove loading css class on navigate if there is pending navigate', (done) => {
		const containsLoadingCssClass = () => {
			return document.documentElement.classList.contains(
				app.getLoadingCssClass()
			);
		};
		app = new App();
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));
		app.once('startNavigate', () => {
			app.once('startNavigate', () =>
				expect(containsLoadingCssClass()).toBe(true)
			);
			app.once('endNavigate', () =>
				expect(containsLoadingCssClass()).toBe(true)
			);
			app.navigate('/path2').then(() => {
				expect(containsLoadingCssClass()).toBe(false);
				done();
			});
		});
		app.navigate('/path1');
	});

	it('does not navigate to unrouted paths', (done) => {
		app = new App();
		app.on('endNavigate', (payload) => {
			expect(payload.error).toBeInstanceOf(Error);
		});
		app.navigate('/path', true).catch((reason) => {
			expect(reason).toBeInstanceOf(Error);
			done();
		});
	});

	it('stores scroll position on page scroll', (done) => {
		app = new App();
		setTimeout(() => {
			expect(window.history.state.scrollTop).toBe(100);
			expect(window.history.state.scrollLeft).toBe(100);
			done();
		}, 300);
		window.scrollTo(100, 100);
	});

	it('does not store page scroll position during navigate', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.on('startNavigate', () => {
			app.onScroll_(); // Coverage
			expect(app.captureScrollPositionFromScrollEvent).toBe(false);
		});
		expect(app.captureScrollPositionFromScrollEvent).toBe(true);
		app.navigate('/path').then(() => {
			expect(app.captureScrollPositionFromScrollEvent).toBe(true);
			done();
		});
	});

	it('updates scroll position on navigate', (done) => {
		app = new App();
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));
		app.navigate('/path1').then(() => {
			setTimeout(() => {
				app.navigate('/path2').then(() => {
					expect(window.pageXOffset).toBe(0);
					expect(window.pageYOffset).toBe(0);
					done();
				});
			}, 300);
			window.scrollTo(100, 100);
		});
	});

	it('does not update scroll position on navigate if updateScrollPosition is disabled', (done) => {
		app = new App();
		app.setUpdateScrollPosition(false);
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));
		app.navigate('/path1').then(() => {
			setTimeout(() => {
				app.navigate('/path2').then(() => {
					expect(window.pageXOffset).toBe(100);
					expect(window.pageYOffset).toBe(100);
					done();
				});
			}, 100);
			window.scrollTo(100, 100);
		});
	});

	it('restores scroll position on navigate back', (done) => {
		app = new App();
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));
		app.navigate('/path1').then(() => {
			setTimeout(() => {
				app.navigate('/path2').then(() => {
					expect(window.pageXOffset).toBe(0);
					expect(window.pageYOffset).toBe(0);
					app.once('endNavigate', () => {
						expect(window.pageXOffset).toBe(100);
						expect(window.pageYOffset).toBe(100);
						done();
					});
					window.history.back();
				});
			}, 300);
			window.scrollTo(100, 100);
		});
	});

	it.skip('dispatches navigate to current path', (done) => {
		window.history.replaceState({}, '', '/path1?foo=1#hash');
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.on('endNavigate', (payload) => {
			expect(payload.path).toBe('/path1?foo=1#hash');
			window.history.replaceState({}, '', getCurrentBrowserPath());
			done();
		});
		app.dispatch();
	});

	it('prevents navigation when beforeDeactivate returns "true"', (done) => {
		class NoNavigateScreen extends Screen {
			beforeDeactivate() {
				return true;
			}
		}
		app = new App();
		app.addRoutes(new Route('/path1', NoNavigateScreen));
		app.addRoutes(new Route('/path2', Screen));
		app.navigate('/path1').then(() => {
			app.on('endNavigate', (payload) => {
				expect(payload.error).toBeInstanceOf(Error);
			});
			app.navigate('/path2').catch((reason) => {
				expect(reason).toBeInstanceOf(Error);
				done();
			});
		});
	});

	it('prevents navigation when beforeDeactivate resolves to "true"', (done) => {
		class NoNavigateScreen extends Screen {
			beforeDeactivate() {
				return new Promise((resolve) => {
					resolve(true);
				});
			}
		}
		app = new App();
		app.addRoutes(new Route('/path1', NoNavigateScreen));
		app.addRoutes(new Route('/path2', Screen));
		app.navigate('/path1').then(() => {
			app.on('endNavigate', (payload) => {
				expect(payload.error).toBeInstanceOf(Error);
			});
			app.navigate('/path2').catch((reason) => {
				expect(reason).toBeInstanceOf(Error);
				done();
			});
		});
	});

	it('prevents navigation when beforeActivate returns "true"', (done) => {
		class NoNavigateScreen extends Screen {
			beforeActivate() {
				return true;
			}
		}

		app = new App();
		app.addRoutes(new Route('/path', NoNavigateScreen));
		app.on('endNavigate', (payload) => {
			expect(payload.error).toBeInstanceOf(Error);
		});
		app.navigate('/path')
			.then(() => done.fail())
			.catch((reason) => {
				expect(reason).toBeInstanceOf(Error);
				expect(reason.message).toBe('Cancelled by next screen');

				done();
			});
	});

	it('prevents navigation when beforeActivate promise resolves to "true"', (done) => {
		class NoNavigateScreen extends Screen {
			beforeActivate() {
				return new Promise((resolve) => {
					resolve(true);
				});
			}
		}

		app = new App();
		app.addRoutes(new Route('/path', NoNavigateScreen));
		app.on('endNavigate', (payload) => {
			expect(payload.error).toBeInstanceOf(Error);
		});
		app.navigate('/path')
			.then(() => done.fail())
			.catch((reason) => {
				expect(reason).toBeInstanceOf(Error);
				expect(reason.message).toBe('Cancelled by next screen');

				done();
			});
	});

	it('prefetches paths', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.prefetch('/path').then(() => {
			expect(app.screens['/path']).toBeInstanceOf(Screen);
			done();
		});
	});

	it('prefetches fail on navigate to unrouted paths', (done) => {
		app = new App();
		app.on('endNavigate', (payload) => {
			expect(payload.error).toBeInstanceOf(Error);
		});
		app.prefetch('/path').catch((reason) => {
			expect(reason).toBeInstanceOf(Error);
			done();
		});
	});

	it.skip('cancels prefetch', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.on('endNavigate', (payload) => {
			expect(payload.error).toBeInstanceOf(Error);
		});
		app.prefetch('/path')
			.catch((reason) => {
				expect(reason).toBeInstanceOf(Error);
				done();
			})
			.cancel();
	});

	it('navigates when clicking on routed links', async () => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));

		jest.spyOn(app, 'updateHistory_').mockImplementation(() => {});
		jest.spyOn(app, 'maybeUpdateScrollPositionState_').mockImplementation(
			() => {}
		);
		jest.spyOn(app, 'syncScrollPositionSyncThenAsync_').mockImplementation(
			() => {}
		);

		const link = enterDocumentLinkElement('/path');

		await userEvent.click(link);

		expect(app.navigationStrategy).toBe('immediate');

		exitDocumentLinkElement();
	});

	it('does not navigate when clicking on target blank links', async () => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		const link = enterDocumentLinkElement('/path');
		link.setAttribute('target', '_blank');
		link.addEventListener('click', (event) => event.preventDefault());
		await userEvent.click(link);
		exitDocumentLinkElement();
		expect(app.pendingNavigate).toBeNull();
	});

	it('passes original event object to "beforeNavigate" when a link is clicked', async () => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));

		jest.spyOn(app, 'updateHistory_').mockImplementation(() => {});
		jest.spyOn(app, 'maybeUpdateScrollPositionState_').mockImplementation(
			() => {}
		);
		jest.spyOn(app, 'syncScrollPositionSyncThenAsync_').mockImplementation(
			() => {}
		);

		app.on('beforeNavigate', (data) => {
			expect(data.event).toBeTruthy();
			expect(data.event.type).toBe('click');
		});
		await userEvent.click(enterDocumentLinkElement('/path'));
		exitDocumentLinkElement();

		expect(window.location.pathname).not.toBe('/path');
	});

	it('prevents navigation on both senna and the browser via beforeNavigate', async () => {
		app = new App();
		app.addRoutes(new Route('/preventedPath', Screen));
		app.on('beforeNavigate', (data, event) => {
			data.event.preventDefault();
			event.preventDefault();
		});
		await userEvent.click(enterDocumentLinkElement('/preventedPath'));
		exitDocumentLinkElement();

		expect(window.location.pathname).not.toBe('/preventedPath');
	});

	it('does not navigate when clicking on external links', async () => {
		const link = enterDocumentLinkElement('http://sennajs.com');
		app = new App();
		app.setAllowPreventNavigate(false);
		link.addEventListener('click', preventDefault);
		await userEvent.click(link);
		expect(app.pendingNavigate).toBeFalsy();
		exitDocumentLinkElement();
	});

	it('does not navigate when clicking on links outside basepath', async () => {
		const link = enterDocumentLinkElement('/path');
		app = new App();
		app.setAllowPreventNavigate(false);
		app.setBasePath('/base');
		link.addEventListener('click', preventDefault);
		await userEvent.click(link);
		expect(app.pendingNavigate).toBeFalsy();
		exitDocumentLinkElement();
	});

	it('does not navigate when clicking on unrouted links', async () => {
		const link = enterDocumentLinkElement('/path');
		app = new App();
		app.setAllowPreventNavigate(false);
		link.addEventListener('click', preventDefault);
		await userEvent.click(link);
		expect(app.pendingNavigate).toBeFalsy();
		exitDocumentLinkElement();
	});

	it('does not navigate when clicking on links with invalid mouse button or modifier keys pressed', async () => {
		const link = enterDocumentLinkElement('/path');
		app = new App();
		app.setAllowPreventNavigate(false);
		app.addRoutes(new Route('/path', Screen));

		link.addEventListener('click', preventDefault);

		await userEvent.click(link, {altKey: false});
		await userEvent.click(link, {ctrlKey: false});
		await userEvent.click(link, {metaKey: false});
		await userEvent.click(link, {shiftKey: false});
		await userEvent.click(link, {button: 1});
		await userEvent.click(link, {button: 2});

		expect(app.pendingNavigate).toBeFalsy();
		exitDocumentLinkElement();
	});

	it('does not navigate when navigate fails synchronously', async () => {
		const link = enterDocumentLinkElement('/path');
		app = new App();
		app.setAllowPreventNavigate(false);
		app.addRoutes(new Route('/path', Screen));
		app.navigate = () => {
			throw new Error();
		};
		link.addEventListener('click', preventDefault);
		await userEvent.click(link);
		expect(app.pendingNavigate).toBeFalsy();
		exitDocumentLinkElement();
	});

	it('reloads page on navigate back to a routed page without history state', (done) => {
		app = new App();
		app.reloadPage = jest.fn();
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));
		app.navigate('/path1').then(() => {
			window.history.replaceState(null, null, null);
			app.navigate('/path2').then(() => {
				window.addEventListener(
					'popstate',
					() => {
						expect(app.reloadPage).toHaveBeenCalled();
						done();
					},
					{once: true}
				);
				window.history.back();
			});
		});
	});

	it('updates referrer when Screen history state returns null', (done) => {
		class NullStateScreen extends Screen {
			beforeUpdateHistoryState() {
				return null;
			}
		}
		app = new App();
		app.addRoutes(new Route('/path1', NullStateScreen));
		app.navigate('/path1').then(() => {
			app.navigate('/path1#hash').then(() => {
				window.addEventListener(
					'popstate',
					() => {
						expect(getCurrentBrowserPath(document.referrer)).toBe(
							'/path1'
						);
						done();
					},
					{once: true}
				);
				window.history.back();
			});
		});
	});

	it('does not reload page on navigate back to a routed page with same path containing hashbang without history state', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.reloadPage = jest.fn();
		app.navigate('/path').then(() => {
			window.location.hash = 'hash1';
			window.history.replaceState(null, null, null);
			app.navigate('/path').then(() => {
				window.addEventListener(
					'popstate',
					() => {
						expect(app.reloadPage).not.toHaveBeenCalled();
						done();
					},
					{once: true}
				);
				window.history.back();
			});
		});
	});

	it('reloads page on navigate back to a routed page with different path containing hashbang without history state', (done) => {
		app = new App();
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));
		app.reloadPage = jest.fn();
		app.navigate('/path1').then(() => {
			window.location.hash = 'hash1';
			window.history.replaceState(null, null, null);
			app.navigate('/path2').then(() => {
				window.addEventListener(
					'popstate',
					() => {
						expect(app.reloadPage).toHaveBeenCalled();
						done();
					},
					{once: true}
				);
				window.history.back();
			});
		});
	});

	it('does not reload page on clicking links with same path containing different hashbang without history state', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.reloadPage = jest.fn();
		window.history.replaceState(null, null, '/path#hash1');
		window.addEventListener(
			'popstate',
			() => {
				expect(app.reloadPage).not.toHaveBeenCalled();
				done();
			},
			{once: true}
		);
		fireEvent(window, new PopStateEvent('popstate'));
	});

	it('does not navigate on clicking links when onbeforeunload returns truthy value', async () => {
		const beforeunload = jest.fn();
		window.onbeforeunload = beforeunload;
		app = new App();

		jest.spyOn(app, 'updateHistory_').mockImplementation(() => {});
		jest.spyOn(app, 'maybeUpdateScrollPositionState_').mockImplementation(
			() => {}
		);
		jest.spyOn(app, 'syncScrollPositionSyncThenAsync_').mockImplementation(
			() => {}
		);

		app.addRoutes(new Route('/path', Screen));
		const link = enterDocumentLinkElement('/path');
		await await userEvent.click(link);
		exitDocumentLinkElement();
		expect(beforeunload).toHaveBeenCalled();
	});

	it('does not navigate back to the previous page on navigate back when onbeforeunload returns a truthy value', (done) => {
		const beforeunload = jest.fn();
		window.onbeforeunload = beforeunload;
		app = new App();
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));
		app.navigate('/path1').then(() => {
			app.navigate('/path2').then(() => {
				window.history.back();

				// assumes that the path must remain the same

				expect(app.activePath).toBe('/path2');
				expect(beforeunload).toHaveBeenCalled();
				done();
			});
		});
	});

	it.skip('respositions scroll to hashed anchors on hash popstate', (done) => {
		const link = enterDocumentLinkElement('/path');
		link.style.position = 'absolute';
		link.style.top = '1000px';
		link.style.left = '1000px';
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.navigate('/path').then(() => {
			window.location.hash = 'link';
			window.history.replaceState(null, null, null);
			window.location.hash = 'other';
			window.history.replaceState(null, null, null);
			window.addEventListener(
				'popstate',
				() => {
					expect(window.pageXOffset).toBe(1000);
					expect(window.pageYOffset).toBe(1000);
					exitDocumentLinkElement();

					window.addEventListener(
						'popstate',
						() => {
							done();
						},
						{once: true}
					);
					window.history.back();
				},
				{once: true}
			);
			window.history.back();
		});
	});

	it('navigates when submitting routed forms', (done) => {
		app = new App();

		jest.spyOn(app, 'updateHistory_').mockImplementation(() => {});
		jest.spyOn(app, 'maybeUpdateScrollPositionState_').mockImplementation(
			() => {}
		);
		jest.spyOn(app, 'syncScrollPositionSyncThenAsync_').mockImplementation(
			() => {}
		);

		app.addRoutes(new Route('/path', Screen));
		const form = enterDocumentFormElement('/path', 'post');
		fireEvent.submit(form);
		expect(app.pendingNavigate).toBeTruthy();

		app.on('endNavigate', () => {
			form.remove();
			done();
		});
	});

	it('does not navigate when submitting routed forms if submit event was prevented', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		const form = enterDocumentFormElement('/path', 'post');

		form.addEventListener(
			'submit',
			(event) => {
				event.preventDefault();
				expect(app.pendingNavigate).toBeFalsy();
				form.remove();
				done();
			},
			{once: true}
		);
		fireEvent.submit(form);
	});

	it('does not capture form element when submit event was prevented', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		const form = enterDocumentFormElement('/path', 'post');

		form.addEventListener(
			'submit',
			(event) => {
				event.preventDefault();
				expect(Liferay.SPA.__capturedFormElement__).toBeFalsy();
				form.remove();
				done();
			},
			{once: true}
		);

		fireEvent.submit(form);
	});

	it('exposes form reference in event data when submitting routed forms', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		const form = enterDocumentFormElement('/path', 'post');
		fireEvent.submit(form);
		app.on('startNavigate', (data) => {
			expect(data.form).toBeTruthy();
		});
		app.on('endNavigate', (data) => {
			expect(data.form).toBeTruthy();
			form.remove();
			done();
		});
	});

	it('does not navigate when submitting forms with method get', () => {
		const form = enterDocumentFormElement('/path', 'get');
		app = new App();
		app.setAllowPreventNavigate(false);
		app.addRoutes(new Route('/path', Screen));
		form.addEventListener('submit', preventDefault);
		fireEvent.submit(form);
		expect(app.pendingNavigate).toBeFalsy();
		form.remove();
	});

	it('does not navigate when submitting on external forms', () => {
		const form = enterDocumentFormElement('http://sennajs.com', 'post');
		app = new App();
		app.setAllowPreventNavigate(false);
		form.addEventListener('submit', preventDefault);
		fireEvent.submit(form);
		expect(app.pendingNavigate).toBeFalsy();
		form.remove();
	});

	it('does not navigate when submitting on forms outside basepath', () => {
		const form = enterDocumentFormElement('/path', 'post');
		app = new App();
		app.setAllowPreventNavigate(false);
		app.setBasePath('/base');
		form.addEventListener('submit', preventDefault);
		fireEvent.submit(form);
		expect(app.pendingNavigate).toBeFalsy();
		form.remove();
	});

	it('does not navigate when submitting on unrouted forms', () => {
		const form = enterDocumentFormElement('/path', 'post');
		app = new App();
		app.setAllowPreventNavigate(false);
		form.addEventListener('submit', preventDefault);
		fireEvent.submit(form);
		expect(app.pendingNavigate).toBeFalsy();
		form.remove();
	});

	it('does not capture form if navigate fails when submitting forms', () => {
		const form = enterDocumentFormElement('/path', 'post');
		app = new App();
		app.setAllowPreventNavigate(false);
		form.addEventListener('submit', preventDefault);
		fireEvent.submit(form);
		expect(Liferay.SPA.__capturedFormElement__).toBeFalsy();
		form.remove();
	});

	it('captures form on beforeNavigate', (done) => {
		const form = enterDocumentFormElement('/path', 'post');
		app = new App();

		jest.spyOn(app, 'updateHistory_').mockImplementation(() => {});
		jest.spyOn(app, 'maybeUpdateScrollPositionState_').mockImplementation(
			() => {}
		);
		jest.spyOn(app, 'syncScrollPositionSyncThenAsync_').mockImplementation(
			() => {}
		);

		app.setAllowPreventNavigate(false);
		app.addRoutes(new Route('/path', Screen));
		app.on('beforeNavigate', (event) => {
			expect(event.form).toBeTruthy();
			form.remove();
			expect(Liferay.SPA.__capturedFormElement__).toBeTruthy();
			Liferay.SPA.__capturedFormElement__ = null;
			done();
		});
		form.addEventListener('submit', jest.fn());
		fireEvent.submit(form);
	});

	it('captures form button when submitting', (done) => {
		const form = enterDocumentFormElement('/path', 'post');
		const button = document.createElement('button');
		form.appendChild(button);
		app = new App();

		jest.spyOn(app, 'updateHistory_').mockImplementation(() => {});
		jest.spyOn(app, 'maybeUpdateScrollPositionState_').mockImplementation(
			() => {}
		);
		jest.spyOn(app, 'syncScrollPositionSyncThenAsync_').mockImplementation(
			() => {}
		);

		app.setAllowPreventNavigate(false);
		app.addRoutes(new Route('/path', StubScreen));

		app.on('beforeNavigate', () => {
			expect(Liferay.SPA.__capturedFormButtonElement__).toBeTruthy();
			form.remove();
			Liferay.SPA.__capturedFormElement__ = null;
			Liferay.SPA.__capturedFormButtonElement__ = null;
			done();
		});

		fireEvent.submit(form);
	});

	it('captures form button when clicking submit button', () => {
		const form = enterDocumentFormElement('/path', 'post');
		const button = document.createElement('button');
		button.type = 'submit';
		button.tabindex = 1;
		form.appendChild(button);
		app = new App();

		jest.spyOn(app, 'updateHistory_').mockImplementation(() => {});
		jest.spyOn(app, 'maybeUpdateScrollPositionState_').mockImplementation(
			() => {}
		);
		jest.spyOn(app, 'syncScrollPositionSyncThenAsync_').mockImplementation(
			() => {}
		);

		app.setAllowPreventNavigate(false);
		app.addRoutes(new Route('/path', Screen));
		button.click();
		expect(Liferay.SPA.__capturedFormButtonElement__).toBeTruthy();
		Liferay.SPA.__capturedFormButtonElement__ = null;
		form.remove();
	});

	it('sets redirect path if history path was redirected', (done) => {
		class RedirectScreen extends Screen {
			beforeUpdateHistoryPath() {
				return '/redirect';
			}
		}
		app = new App();
		app.addRoutes(new Route('/path', RedirectScreen));
		app.navigate('/path').then(() => {
			expect(app.redirectPath).toBe('/redirect');
			expect(app.activePath).toBe('/path');
			done();
		});
	});

	it('updates the state with the redirected path', (done) => {
		class RedirectScreen extends Screen {
			beforeUpdateHistoryPath() {
				return '/redirect';
			}
		}
		app = new App();
		app.addRoutes(new Route('/path', RedirectScreen));
		app.navigate('/path').then(() => {
			expect(window.location.pathname).toBe('/redirect');
			done();
		});
	});

	it.skip('restores hashbang if redirect path is the same as requested path', (done) => {
		class RedirectScreen extends Screen {
			beforeUpdateHistoryPath() {
				return '/path';
			}
		}
		app = new App();
		app.addRoutes(new Route('/path', RedirectScreen));
		app.navigate('/path#hash').then(() => {
			expect(getCurrentBrowserPath()).toBe('/path#hash');
			done();
		});
	});

	it.skip('does not restore hashbang if redirect path is not the same as requested path', (done) => {
		class RedirectScreen extends Screen {
			beforeUpdateHistoryPath() {
				return '/redirect';
			}
		}
		app = new App();
		app.addRoutes(new Route('/path', RedirectScreen));
		app.navigate('/path#hash').then(() => {
			expect(getCurrentBrowserPath()).toBe('/redirect');
			done();
		});
	});

	it('does skipLoadPopstate before page is loaded', (done) => {
		app = new App();
		app.onLoad_(); // Simulate
		expect(app.skipLoadPopstate).toBe(true);
		setTimeout(() => {
			expect(app.skipLoadPopstate).toBe(false);
			done();
		});
	});

	it('respects screen lifecycle on navigate', (done) => {
		class StubScreen2 extends Screen {}
		StubScreen2.prototype.activate = jest.fn();
		StubScreen2.prototype.beforeDeactivate = jest.fn();
		StubScreen2.prototype.deactivate = jest.fn();
		StubScreen2.prototype.evaluateScripts = jest.fn();
		StubScreen2.prototype.evaluateStyles = jest.fn();
		StubScreen2.prototype.flip = jest.fn();
		StubScreen2.prototype.load = jest
			.fn()
			.mockImplementation(() => Promise.resolve());

		app = new App();

		jest.spyOn(app, 'updateHistory_').mockImplementation(() => {});
		jest.spyOn(app, 'maybeUpdateScrollPositionState_').mockImplementation(
			() => {}
		);
		jest.spyOn(app, 'syncScrollPositionSyncThenAsync_').mockImplementation(
			() => {}
		);

		app.addRoutes(new Route('/path1', StubScreen));
		app.addRoutes(new Route('/path2', StubScreen2));

		app.navigate('/path1').then(() => {
			app.navigate('/path2').then(() => {
				const lifecycleOrder = [
					StubScreen.prototype.load,
					StubScreen.prototype.flip,
					StubScreen.prototype.evaluateStyles,
					StubScreen.prototype.evaluateScripts,
					StubScreen.prototype.activate,
					StubScreen.prototype.beforeDeactivate,
					StubScreen2.prototype.load,
					StubScreen.prototype.deactivate,
					StubScreen2.prototype.flip,
					StubScreen2.prototype.evaluateStyles,
					StubScreen2.prototype.evaluateScripts,
					StubScreen2.prototype.activate,
					StubScreen.prototype.disposeInternal,
				];
				for (let i = 1; i < lifecycleOrder.length - 1; i++) {
					expect(
						lifecycleOrder[i - 1].mock.invocationCallOrder[0]
					).toBeLessThan(
						lifecycleOrder[i].mock.invocationCallOrder[0]
					);
				}

				done();
			});
		});
	});

	it('respects screen lifecycle on navigate, preload styles', (done) => {
		class StubScreen2 extends Screen {}
		StubScreen2.prototype.activate = jest.fn();
		StubScreen2.prototype.beforeDeactivate = jest.fn();
		StubScreen2.prototype.deactivate = jest.fn();
		StubScreen2.prototype.evaluateScripts = jest.fn();
		StubScreen2.prototype.evaluateStyles = jest.fn();
		StubScreen2.prototype.flip = jest.fn();
		StubScreen2.prototype.load = jest
			.fn()
			.mockImplementation(() => Promise.resolve());
		StubScreen2.prototype.preloadStyles = jest.fn();

		app = new App({preloadCSS: true});

		jest.spyOn(app, 'updateHistory_').mockImplementation(() => {});
		jest.spyOn(app, 'maybeUpdateScrollPositionState_').mockImplementation(
			() => {}
		);
		jest.spyOn(app, 'syncScrollPositionSyncThenAsync_').mockImplementation(
			() => {}
		);

		app.addRoutes(new Route('/path1', StubScreen));
		app.addRoutes(new Route('/path2', StubScreen2));

		app.navigate('/path1').then(() => {
			app.navigate('/path2').then(() => {
				const lifecycleOrder = [
					StubScreen.prototype.load,
					StubScreen.prototype.preloadStyles,
					StubScreen.prototype.evaluateStyles,
					StubScreen.prototype.flip,
					StubScreen.prototype.evaluateScripts,
					StubScreen.prototype.activate,
					StubScreen.prototype.beforeDeactivate,
					StubScreen2.prototype.load,
					StubScreen.prototype.deactivate,
					StubScreen2.prototype.preloadStyles,
					StubScreen2.prototype.evaluateStyles,
					StubScreen2.prototype.flip,
					StubScreen2.prototype.evaluateScripts,
					StubScreen2.prototype.activate,
					StubScreen.prototype.disposeInternal,
				];
				for (let i = 1; i < lifecycleOrder.length - 1; i++) {
					expect(
						lifecycleOrder[i - 1].mock.invocationCallOrder[0]
					).toBeLessThan(
						lifecycleOrder[i].mock.invocationCallOrder[0]
					);
				}

				done();
			});
		});
	});

	it('renders surfaces', (done) => {
		class ContentScreen extends Screen {
			getSurfaceContent(surfaceId) {
				return surfaceId;
			}
			getId() {
				return 'screenId';
			}
		}
		const surface = new Surface('surfaceId');
		surface.addContent = jest.fn();
		app = new App();
		app.addRoutes(new Route('/path', ContentScreen));
		app.addSurfaces(surface);
		app.navigate('/path').then(() => {
			expect(surface.addContent).toHaveBeenCalledWith(
				'screenId',
				'surfaceId'
			);
			done();
		});
	});

	it('passes extracted params to "getSurfaceContent"', (done) => {
		let screen;
		class ContentScreen extends Screen {
			constructor() {
				super();
				screen = this;
			}

			getId() {
				return 'screenId';
			}
		}
		ContentScreen.prototype.getSurfaceContent = jest.fn();

		const surface = new Surface('surfaceId');
		app = new App();
		app.addRoutes(new Route('/path/:foo(\\d+)/:bar', ContentScreen));
		app.addSurfaces(surface);
		app.navigate('/path/123/abc').then(() => {
			expect(screen.getSurfaceContent).toHaveBeenCalledWith('surfaceId', {
				bar: 'abc',
				foo: '123',
			});
			done();
		});
	});

	it('passes extracted params to "getSurfaceContent" with base path', (done) => {
		let screen;
		class ContentScreen extends Screen {
			constructor() {
				super();
				screen = this;
			}

			getId() {
				return 'screenId';
			}
		}
		ContentScreen.prototype.getSurfaceContent = jest.fn();

		const surface = new Surface('surfaceId');
		app = new App();
		app.setBasePath('/path');
		app.addRoutes(new Route('/:foo(\\d+)/:bar', ContentScreen));
		app.addSurfaces(surface);
		app.navigate('/path/123/abc').then(() => {
			expect(screen.getSurfaceContent).toHaveBeenCalledWith('surfaceId', {
				bar: 'abc',
				foo: '123',
			});
			done();
		});
	});

	it('extracts params for the given route and path', () => {
		app = new App();
		app.setBasePath('/path');
		const route = new Route('/:foo(\\d+)/:bar', () => {});
		const params = app.extractParams(route, '/path/123/abc');

		expect(params).toEqual({
			bar: 'abc',
			foo: '123',
		});
	});

	it('renders default surface content when not provided by screen', (done) => {
		class ContentScreen1 extends Screen {
			getSurfaceContent(surfaceId) {
				if (surfaceId === 'surfaceId1') {
					return 'content1';
				}
			}
			getId() {
				return 'screenId1';
			}
		}
		class ContentScreen2 extends Screen {
			getSurfaceContent(surfaceId) {
				if (surfaceId === 'surfaceId2') {
					return 'content2';
				}
			}
			getId() {
				return 'screenId2';
			}
		}
		document.body.appendChild(
			buildFragment(
				'<div id="surfaceId1"><div id="surfaceId1-default">default1</div></div>'
			)
		);
		document.body.appendChild(
			buildFragment(
				'<div id="surfaceId2"><div id="surfaceId2-default">default2</div></div>'
			)
		);
		const surface1 = new Surface('surfaceId1');
		const surface2 = new Surface('surfaceId2');
		surface1.addContent = jest.fn();
		surface2.addContent = jest.fn();
		app = new App();
		app.addRoutes(new Route('/path1', ContentScreen1));
		app.addRoutes(new Route('/path2', ContentScreen2));
		app.addSurfaces([surface1, surface2]);
		app.navigate('/path1').then(() => {
			expect(surface1.addContent).toHaveBeenCalledWith(
				'screenId1',
				'content1'
			);

			expect(surface2.addContent).toHaveBeenCalledWith(
				'screenId1',
				undefined
			);
			expect(surface2.getChild('default').innerHTML).toBe('default2');

			app.navigate('/path2').then(() => {
				expect(surface1.addContent).toHaveBeenCalledWith(
					'screenId2',
					undefined
				);
				expect(surface1.getChild('default').innerHTML).toBe('default1');

				expect(surface2.addContent).toHaveBeenCalledWith(
					'screenId2',
					'content2'
				);

				surface1.getElement().remove();
				surface2.getElement().remove();
				done();
			});
		});
	});

	it('adds surface content after history path is updated', (done) => {
		const surface = new Surface('surfaceId');
		surface.addContent = () => {
			expect(window.location.pathname).toBe('/path');
		};
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.addSurfaces(surface);
		app.navigate('/path').then(() => {
			done();
		});
	});

	it('navigates cancelling navigation to multiple paths after navigation is scheduled to keep only the last one', (done) => {
		app = new App();

		class TestScreen extends Screen {
			evaluateStyles(surfaces) {
				userEvent.click(enterDocumentLinkElement('/path2'));
				exitDocumentLinkElement();

				return super.evaluateStyles(surfaces);
			}

			evaluateScripts(surfaces) {
				expect(app.isNavigationPending).toBeTruthy();

				return super.evaluateScripts(surfaces);
			}
		}

		class TestScreen2 extends Screen {
			evaluateStyles(surfaces) {
				userEvent.click(enterDocumentLinkElement('/path3'));
				exitDocumentLinkElement();

				return super.evaluateStyles(surfaces);
			}

			evaluateScripts(surfaces) {
				expect(app.isNavigationPending).toBeTruthy();

				return super.evaluateScripts(surfaces);
			}
		}

		const startNavigateStub = jest.fn();
		const endNavigateStub = jest.fn();
		app.stopPendingNavigate_ = jest.fn();

		app.on('startNavigate', startNavigateStub);
		app.on('endNavigate', endNavigateStub);

		app.addRoutes(new Route('/path1', TestScreen));
		app.addRoutes(new Route('/path2', TestScreen2));
		app.addRoutes(new Route('/path3', TestScreen2));

		app.navigate('/path1');
		app.navigate('/path2');

		app.navigate('/path1').then(() => {
			expect(startNavigateStub).toHaveBeenCalledTimes(3);
			expect(endNavigateStub).toHaveBeenCalledTimes(3);
			expect(app.stopPendingNavigate_).toHaveBeenCalledTimes(3);

			expect(app.isNavigationPending).toBeFalsy();

			done();
		});
	});

	it('navigates cancelling previous navigation when navigation is to the same path', (done) => {
		app = new App();

		class TestScreen extends Screen {
			load(path) {
				userEvent.click(enterDocumentLinkElement('/path1'));
				exitDocumentLinkElement();

				return super.load(path);
			}
		}

		const startNavigateStub = jest.fn();
		const endNavigateStub = jest.fn();
		app.stopPendingNavigate_ = jest.fn();

		app.on('startNavigate', startNavigateStub);
		app.on('endNavigate', endNavigateStub);

		app.addRoutes(new Route('/path1', TestScreen));

		app.navigate('/path1');
		app.navigate('/path1');
		app.navigate('/path1');
		app.navigate('/path1');

		app.navigate('/path1').then(() => {
			expect(startNavigateStub).toHaveBeenCalledTimes(1);
			expect(endNavigateStub).toHaveBeenCalledTimes(1);

			expect(app.stopPendingNavigate_).toHaveBeenCalledTimes(1);

			expect(app.isNavigationPending).toBeFalsy();

			done();
		});
	});

	it('sets document title from screen title', (done) => {
		class TitledScreen extends Screen {
			getTitle() {
				return 'title';
			}
		}
		app = new App();
		app.addRoutes(new Route('/path', TitledScreen));
		app.navigate('/path').then(() => {
			expect(document.title).toBe('title');
			done();
		});
	});

	it('sets Liferay.SPA.__capturedFormElement__ to null after navigate', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', Screen));
		app.navigate('/path').then(() => {
			expect(Liferay.SPA.__capturedFormElement__).toBeNull();
			done();
		});
	});

	it.skip('cancels nested promises on canceled navigate', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', HtmlScreen));
		app.navigate('/path')
			.then(() => done.fail())
			.catch(() => {
				expect(fetch.mock.calls.length).toBe(0);
				done();
			})
			.cancel();
	});

	it.skip('cancels nested promises on canceled prefetch', (done) => {
		app = new App();
		app.addRoutes(new Route('/path', HtmlScreen));
		app.prefetch('/path')
			.then(() => done.fail())
			.catch(() => {
				expect(fetch.mock.calls.length).toBe(0);
				done();
			})
			.cancel();
	});

	it.skip('waits for pendingNavigate before removing screen on double back navigation', (done) => {
		class CacheScreen extends Screen {
			constructor() {
				super();
				this.cacheable = true;
			}

			load() {
				return new Promise((resolve) => setTimeout(resolve, 100));
			}
		}

		app = new App();
		app.addRoutes(new Route('/path1', CacheScreen));
		app.addRoutes(new Route('/path2', CacheScreen));
		app.addRoutes(new Route('/path3', CacheScreen));

		app.navigate('/path1')
			.then(() => app.navigate('/path2'))
			.then(() => app.navigate('/path3'))
			.then(() => {
				let pendingNavigate;
				app.on('startNavigate', () => {
					pendingNavigate = app.pendingNavigate;
					expect(app.screens['/path2']).toBeTruthy();
				});
				app.once('endNavigate', () => {
					if (app.isNavigationPending) {
						expect(app.screens['/path2']).toBeFalsy();
						done();
					}
					else {
						pendingNavigate.finally(() => {
							expect(app.screens['/path2']).toBeFalsy();
							done();
						});

						// pendingNavigate.cancel();

					}
				});
				window.history.go(-1);
				setTimeout(() => window.history.go(-1), 50);
			});
	});

	it('scrolls to anchor element on navigate', (done) => {
		document.body.appendChild(
			buildFragment(
				'<div id="element" style="position:absolute;top:400px;"><div id="surfaceId1" style="position:relative;top:400px"></div></div>'
			)
		);

		app = new App();
		app.addRoutes(new Route('/path1', Screen));
		app.addSurfaces(['surfaceId1']);
		app.navigate('/path1#surfaceId1').then(() => {
			const surfaceNode = document.querySelector('#surfaceId1');
			const {offsetLeft, offsetTop} = getNodeOffset(surfaceNode);
			expect(window.pageYOffset).toBe(offsetTop);
			expect(window.pageXOffset).toBe(offsetLeft);
			document.getElementById('element').remove();
			done();
		});
	});

	it('updates the document.referrer upon navigation', (done) => {
		app = new App();
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));
		app.addRoutes(new Route('/path3', Screen));

		app.navigate('/path1')
			.then(() => {
				return app.navigate('/path2');
			})
			.then(() => {
				expect(getUrlPathWithoutHash(document.referrer)).toBe('/path1');

				return app.navigate('/path3');
			})
			.then(() => {
				expect(getUrlPathWithoutHash(document.referrer)).toBe('/path2');
				app.on(
					'endNavigate',
					() => {
						expect(getUrlPathWithoutHash(document.referrer)).toBe(
							'/path1'
						);
						done();
					},
					true
				);
				window.history.back();
			});
	});

	it('does reload page on navigate back to a routed page without history state and skipLoadPopstate is active', () => {
		app = new App();
		app.reloadPage = jest.fn();
		app.addRoutes(new Route('/path1', Screen));
		app.addRoutes(new Route('/path2', Screen));

		return app.navigate('/path1').then(() => {
			window.history.replaceState(null, null, null);

			return app.navigate('/path2').then(() => {
				return new Promise((resolve) => {
					window.addEventListener(
						'popstate',
						() => {
							expect(app.reloadPage).not.toHaveBeenCalled();
							resolve();
						},
						{once: true}
					);
					app.skipLoadPopstate = true;
					window.history.back();
				});
			});
		});
	});
});

function enterDocumentLinkElement(href) {
	document.body.appendChild(
		buildFragment('<a id="link" href="' + href + '">link</a>')
	);

	return document.getElementById('link');
}

function enterDocumentFormElement(action, method) {
	const random = Math.floor(Math.random() * 10000);
	document.body.appendChild(
		buildFragment(
			`<form id="form_${random}" action="${action}" method="${method}" enctype="multipart/form-data"></form>`
		)
	);

	return document.getElementById(`form_${random}`);
}

function exitDocumentLinkElement() {
	document.getElementById('link').remove();
}

function preventDefault(event) {
	event.preventDefault();
}

function mockWindowLocation({
	hash = '#hash',
	host = 'localhost:8080',
	hostname = 'localhost',
	origin = 'http://localhost:8080',
	pathname = '/path',
	port = '8080',
	search = '?a=1',
}) {
	const location = window.location;

	delete window.location;

	window.location = Object.defineProperties(
		{},
		{
			...Object.getOwnPropertyDescriptors(location),

			hash: {
				configurable: true,
				value: hash,
			},

			host: {
				configurable: true,
				value: host,
			},

			hostname: {
				configurable: true,
				value: hostname,
			},

			origin: {
				configurable: true,
				value: origin,
			},

			pathname: {
				configurable: true,
				value: pathname,
			},

			port: {
				configurable: true,
				value: port,
			},

			search: {
				configurable: true,
				value: search,
			},
		}
	);

	return () => {
		window.location = location;
	};
}

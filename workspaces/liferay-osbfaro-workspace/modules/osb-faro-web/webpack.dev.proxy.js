const {promisify} = require('util');
const zlib = require('zlib');

const brotliDecompress = promisify(zlib.brotliDecompress);
const gunzip = promisify(zlib.gunzip);
const inflate = promisify(zlib.inflate);

// The dev server proxies to a remote Liferay (e.g. analytics-stg) whose
// `web.server.host` makes it emit absolute URLs back to itself. The handler
// returned here buffers each response and rewrites those URLs (in Location,
// Set-Cookie Domain, and the body) so the browser stays on the dev server
// instead of leaving for the upstream host.

// Injects an authenticated session Cookie (copied from a browser already logged
// in via Okta against an SSO-protected upstream such as analytics-internal) into
// every proxied request, so the dev server can reach the upstream without
// proxying the Okta redirect flow. A falsy cookie leaves the request untouched.
// Also rewrites Referer and Origin onto the upstream, which endpoints that
// check the referrer require.

function createOnProxyReq(cookie, target) {
	return function onProxyReq(proxyReq, req) {
		if (cookie) {
			proxyReq.setHeader('cookie', cookie);
		}

		// Some endpoints reject a request whose Referer is not the upstream
		// host: `asset-summary` answers 403, so the Top Assets card renders
		// empty locally while it has data on the deployed environment.
		// `changeOrigin` only rewrites Host, leaving Referer and Origin
		// pointing at the dev server, so rewrite them here too.

		const proxyOrigin = req.headers.host && `http://${req.headers.host}`;

		if (!proxyOrigin || !target) {
			return;
		}

		for (const name of ['origin', 'referer']) {
			const value = req.headers[name];

			if (value && value.startsWith(proxyOrigin)) {
				proxyReq.setHeader(
					name,
					target + value.slice(proxyOrigin.length)
				);
			}
		}
	};
}

function createOnProxyRes(target) {
	return async function onProxyRes(proxyRes, req, res) {
		const proxyOrigin = `http://${req.headers.host}`;

		res.statusCode = proxyRes.statusCode;

		for (const [key, value] of Object.entries(proxyRes.headers)) {
			if (
				key === 'content-encoding' ||
				key === 'content-length' ||
				key === 'transfer-encoding'
			) {
				continue;
			}
			if (key === 'location' && value) {
				res.setHeader(
					'location',
					String(value).split(target).join(proxyOrigin)
				);
				continue;
			}
			if (key === 'set-cookie' && value) {
				const cookies = Array.isArray(value) ? value : [value];
				res.setHeader(
					'set-cookie',
					cookies.map((cookie) =>
						cookie.replace(/;\s*Domain=[^;]+/gi, '')
					)
				);
				continue;
			}
			res.setHeader(key, value);
		}

		const chunks = [];

		proxyRes.on('data', (chunk) => chunks.push(chunk));

		proxyRes.on('end', async () => {
			let buffer = Buffer.concat(chunks);

			const encoding = proxyRes.headers['content-encoding'];
			if (buffer.length > 0 && encoding) {
				try {
					if (encoding === 'gzip') {
						buffer = await gunzip(buffer);
					}
					else if (encoding === 'br') {
						buffer = await brotliDecompress(buffer);
					}
					else if (encoding === 'deflate') {
						buffer = await inflate(buffer);
					}
				}
				catch {

					// Upstream announced an encoding but the body could not be
					// decoded (e.g. empty body still carrying Content-Encoding).
					// Fall through with the raw bytes.

				}
			}

			const contentType = proxyRes.headers['content-type'] || '';
			const isTextual =
				contentType.includes('text/html') ||
				contentType.includes('javascript') ||
				contentType.includes('application/json') ||
				contentType.includes('text/css');

			if (isTextual && buffer.length > 0) {
				buffer = Buffer.from(
					buffer.toString('utf8').split(target).join(proxyOrigin)
				);
			}

			res.end(buffer);
		});

		proxyRes.on('error', () => {
			if (!res.headersSent) {
				res.statusCode = 502;
			}

			res.end();
		});
	};
}

module.exports = {createOnProxyReq, createOnProxyRes};

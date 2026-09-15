const {promisify} = require('util');
const zlib = require('zlib');

const brotliDecompress = promisify(zlib.brotliDecompress);
const gunzip = promisify(zlib.gunzip);
const inflate = promisify(zlib.inflate);

// Sends an upstream session cookie, copied from a browser already through Okta,
// so the dev server reaches an SSO-protected backend such as ldp-internal
// without proxying the redirect flow. A falsy cookie leaves the request alone.

function createOnProxyReq(cookie, target) {
	return function onProxyReq(proxyReq, req) {
		if (cookie) {
			proxyReq.setHeader('cookie', cookie);
		}

		// `changeOrigin` rewrites Host alone, and endpoints such as
		// `asset-summary` answer 403 to a foreign Referer.

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

// The upstream's `web.server.host` makes it emit absolute URLs back to itself,
// so each response is buffered and those URLs rewritten onto the dev server.

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

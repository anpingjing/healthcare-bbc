export default async function handler(req: any, res: any) {
  const backendBase = (process.env.BACKEND_URL || process.env.API_BASE_URL || '').trim().replace(/\/+$/, '')
  if (!backendBase) {
    res.statusCode = 500
    res.setHeader('content-type', 'application/json; charset=utf-8')
    res.end(JSON.stringify({ code: 500, message: 'Missing BACKEND_URL env var' }))
    return
  }

  const pathParam = Array.isArray(req.query?.path) ? req.query.path.join('/') : (req.query?.path || '')
  const normalizedBackend = backendBase.endsWith('/api/v1') ? backendBase : `${backendBase}/api/v1`

  const url = new URL(`${normalizedBackend}/${pathParam}`)
  if (req.query) {
    for (const [k, v] of Object.entries(req.query)) {
      if (k === 'path') continue
      if (Array.isArray(v)) {
        for (const item of v) url.searchParams.append(k, String(item))
      } else if (v !== undefined) {
        url.searchParams.set(k, String(v))
      }
    }
  }

  const headers: Record<string, string> = {}
  for (const [k, v] of Object.entries(req.headers || {})) {
    if (!v) continue
    const key = k.toLowerCase()
    if (key === 'host' || key === 'connection' || key === 'content-length') continue
    if (Array.isArray(v)) headers[k] = v.join(',')
    else headers[k] = String(v)
  }

  const method = (req.method || 'GET').toUpperCase()
  const hasBody = !['GET', 'HEAD'].includes(method)
  const body = hasBody ? (typeof req.body === 'string' ? req.body : JSON.stringify(req.body ?? {})) : undefined
  if (hasBody && !headers['content-type']) {
    headers['content-type'] = 'application/json; charset=utf-8'
  }

  const upstream = await fetch(url.toString(), {
    method,
    headers,
    body
  })

  res.statusCode = upstream.status
  upstream.headers.forEach((value, key) => {
    const lower = key.toLowerCase()
    if (lower === 'transfer-encoding' || lower === 'content-encoding') return
    res.setHeader(key, value)
  })

  const buf = Buffer.from(await upstream.arrayBuffer())
  res.end(buf)
}

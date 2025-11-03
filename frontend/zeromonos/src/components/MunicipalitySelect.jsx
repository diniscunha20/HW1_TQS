import { useEffect, useState, useRef } from 'react'
import { fetchMunicipalities } from '../api'

export default function MunicipalitySelect({ value, onChange }) {
  const [query, setQuery] = useState('')
  const [all, setAll] = useState([])
  const [filtered, setFiltered] = useState([])
  const [loading, setLoading] = useState(false)
  const [open, setOpen] = useState(false)
  const boxRef = useRef(null)

  // carrega uma vez
  useEffect(() => {
    let alive = true
    setLoading(true)
    fetchMunicipalities('')
      .then(data => alive && setAll(data))
      .finally(() => alive && setLoading(false))
    return () => { alive = false }
  }, [])

  // filtra conforme a escrita
  useEffect(() => {
    const term = query.trim().toLowerCase()
    if (!term) {
      setFiltered(all)
    } else {
      setFiltered(
        all.filter(m => m.name.toLowerCase().includes(term))
      )
    }
  }, [query, all])

  // fechar dropdown ao clicar fora
  useEffect(() => {
    const handleClick = e => {
      if (boxRef.current && !boxRef.current.contains(e.target)) {
        setOpen(false)
      }
    }
    document.addEventListener('click', handleClick)
    return () => document.removeEventListener('click', handleClick)
  }, [])

  const handleSelect = m => {
    setQuery(m.name)
    onChange(m.code)
    setOpen(false)
  }

  return (
    <div
      className="municipality-select"
      ref={boxRef}
      style={{
        position: 'relative',
        borderRadius: 8,
        background: '#fff',
      }}
    >
      <input
        placeholder="Selecione o município"
        value={query}
        onChange={e => {
          setQuery(e.target.value)
          onChange('') // limpa seleção
          setOpen(true)
        }}
        onFocus={() => setOpen(true)}
        required
        style={{
          width: '100%',
          padding: '8px 10px',
          border: '1px solid #cbd5e1',
          borderRadius: 6,
        }}
      />
      {loading && (
        <div className="loading" style={{ marginTop: 8, fontSize: 12, color: '#64748b' }}>
          A carregar...
        </div>
      )}
      {open && !loading && (
        <ul
          className="dropdown"
          style={{
            position: 'absolute',
            top: '100%',
            left: 0,
            right: 0,
            marginTop: 6,
            listStyle: 'none',
            padding: 0,
            background: '#fff',
            border: '1px solid #e2e8f0',
            borderRadius: 8,
            boxShadow: '0 8px 16px rgba(0,0,0,0.08)',
            maxHeight: 200, // ~5 items
            overflowY: 'auto',
            zIndex: 10,
          }}
        >
          {filtered.length === 0 && (
            <li className="no-results" style={{ padding: '8px 10px', color: '#64748b' }}>
              Nenhum município encontrado
            </li>
          )}
          {filtered.map(m => (
            <li
              key={m.code}
              className={m.code === value ? 'selected' : ''}
              onClick={() => handleSelect(m)}
              style={{ padding: '8px 10px', cursor: 'pointer' }}
            >
              {m.name}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

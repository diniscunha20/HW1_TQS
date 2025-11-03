import {useEffect, useMemo, useState} from 'react'
import MunicipalitySelect from '../components/MunicipalitySelect'
import {
  listBookings,
  updateStatus,
  getGlobalLimits,
  updateGlobalLimits,
  countBookingsByDate
} from '../api'

function ymd(d = new Date()){
  // YYYY-MM-DD em local time
  const pad = (n)=> String(n).padStart(2,'0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}`
}

export default function Staff(){
  // -------- Capacidade global / contagem diária --------
  const [globalLimit, setGlobalLimit] = useState(null)
  const [savingLimit, setSavingLimit] = useState(false)
  const [selectedDate, setSelectedDate] = useState(ymd())
  const [activeCount, setActiveCount] = useState(null)
  const [loadingCount, setLoadingCount] = useState(false)

  // -------- Lista por município/estado --------
  const [municipality,setMunicipality] = useState('')
  const [statusFilter,setStatusFilter] = useState('RECEIVED') // alinhado c/ backend
  const [page,setPage] = useState(0)
  const [items,setItems] = useState([])     // lista a render
  const [total,setTotal] = useState(0)      // total para paginação (se existir)
  const [loading,setLoading] = useState(false)
  const [error,setError] = useState(null)

  const totalPages = useMemo(()=> Math.max(1, Math.ceil(total/20)), [total])

  // --- fetch limite global ao montar ---
  useEffect(() => {
    getGlobalLimits()
      .then(d => setGlobalLimit(d?.maxPerDay ?? null))
      .catch(() => setGlobalLimit(null))
  }, [])

  // --- fetch contagem ativa para a data selecionada ---
  const refreshActiveCount = () => {
    setLoadingCount(true)
    countBookingsByDate(selectedDate, true)
      .then(n => setActiveCount(typeof n === 'number' ? n : Number(n)))
      .catch(() => setActiveCount(null))
      .finally(()=> setLoadingCount(false))
  }
  useEffect(refreshActiveCount, [selectedDate])

  // --- lista por município/estado/página ---
  useEffect(()=>{ 
    if(!municipality) return
    setLoading(true)
    setError(null)
    listBookings({ municipalityCode: municipality, status: statusFilter, page })
      .then(res => {
        if (Array.isArray(res)) {
          setItems(res)
          setTotal(res.length)
        } else if (res && Array.isArray(res.items)) {
          setItems(res.items)
          setTotal(typeof res.total === 'number' ? res.total : res.items.length)
        } else {
          setItems([])
          setTotal(0)
        }
      })
      .catch(err => {
        console.error('Erro a carregar bookings:', err)
        setError('Não foi possível carregar os pedidos.')
        setItems([])
        setTotal(0)
      })
      .finally(() => setLoading(false))
  },[municipality,statusFilter,page])

  // --- ação: atualizar estado e refazer lista + contador ---
  const set = (token, nextStatus)=> {
    updateStatus(token, nextStatus)
      .then(() => Promise.all([
        listBookings({ municipalityCode: municipality, status: statusFilter, page }),
        countBookingsByDate(selectedDate, true) // atualiza contador do topo
      ]))
      .then(([res, n]) => {
        if (Array.isArray(res)) { setItems(res); setTotal(res.length) }
        else { setItems(res.items || []); setTotal(res.total || (res.items?.length || 0)) }
        setActiveCount(typeof n === 'number' ? n : Number(n))
      })
      .catch(err => {
        console.error('Erro ao atualizar estado:', err)
        setError('Falha ao atualizar o estado do pedido.')
      })
  }

  const getStatusClass = (status) => `status-badge status-${String(status || '').toLowerCase()}`

  return (
    <div>
      {/* -------- Definições globais -------- */}
      <div className="card">
        <h2>⚙️ Capacidade diária (global)</h2>

        <div style={{display:'flex', gap:'1rem', alignItems:'center', flexWrap:'wrap', marginBottom:12}}>
          <label>Máximo global por dia</label>
          <input
            type="number"
            min={1}
            value={globalLimit ?? ''}
            onChange={e=> setGlobalLimit(e.target.value ? Number(e.target.value) : null)}
            style={{width:'8rem'}}
          />
          <button
            className="btn-success"
            disabled={globalLimit == null || savingLimit}
            onClick={async () => {
              try {
                setSavingLimit(true)
                await updateGlobalLimits(globalLimit)
                // opcional: confirmar com um refetch
                const d = await getGlobalLimits()
                setGlobalLimit(d?.maxPerDay ?? null)
                // se mudares o limite de HOJE, faz sentido refazer a contagem visualmente
                refreshActiveCount()
              } catch {
                alert('Falha ao guardar o limite global')
              } finally {
                setSavingLimit(false)
              }
            }}
          >
            Guardar
          </button>
        </div>

        <div style={{display:'flex', gap:'1rem', alignItems:'center', flexWrap:'wrap'}}>
          <label>Data</label>
          <input
            type="date"
            value={selectedDate}
            onChange={e=> setSelectedDate(e.target.value)}
          />
          <div>
            <div style={{color:'#7f8c8d'}}>Pedidos ativos neste dia</div>
            <strong>{loadingCount ? '…' : (activeCount ?? '-')}</strong>
            {globalLimit != null && (
              <span style={{marginLeft:8, color:'#6b7280'}}>
                / {globalLimit}
              </span>
            )}
          </div>
        </div>
      </div>

      {/* -------- Filtro por município -------- */}
      <div className="card">
        <h2>📋 Gestão de Pedidos</h2>

        <div className="form-group">
          <label>Município</label>
          <MunicipalitySelect value={municipality} onChange={val => { setPage(0); setMunicipality(val) }} />
        </div>

        <div className="form-group">
          <label>Filtrar por Estado</label>
          <select
            value={statusFilter}
            onChange={e=>{ setPage(0); setStatusFilter(e.target.value) }}
          >
            <option value="RECEIVED">Pendente</option>
            <option value="CONFIRMED">Confirmado</option>
            <option value="IN_PROGRESS">Em Progresso</option>
            <option value="COMPLETED">Concluído</option>
            <option value="CANCELLED">Cancelado</option>
          </select>
        </div>
      </div>

      {loading && <p style={{textAlign: 'center'}}>A carregar...</p>}
      {error && <div className="card" style={{color:'#c0392b'}}>{error}</div>}

      {!loading && !error && items.length === 0 && municipality && (
        <div className="card">
          <p style={{textAlign: 'center', color: '#95a5a6'}}>Nenhum pedido encontrado.</p>
        </div>
      )}

      {!loading && !error && items.length > 0 && (
        <div className="card">
          <table>
            <thead>
              <tr>
                <th>Token</th>
                <th>Nome</th>
                <th>Data</th>
                <th>Período</th>
                <th>Estado</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {items.map(it=>(
                <tr key={it.token || it.id || `${it.date}-${it.name}`}>
                  <td><strong>{it.token}</strong></td>
                  <td>{it.name}</td>
                  <td>{it.date}</td>
                  <td>{it.timeSlot}</td>
                  <td><span className={getStatusClass(it.status)}>{it.status}</span></td>
                  <td>
                    <button className="btn-info" onClick={()=>set(it.token,'CONFIRMED')}>Confirmar</button>
                    <button className="btn-warning" onClick={()=>set(it.token,'IN_PROGRESS')}>Em Curso</button>
                    <button className="btn-success" onClick={()=>set(it.token,'COMPLETED')}>Concluir</button>
                    <button className="btn-danger" onClick={()=>set(it.token,'CANCELLED')}>Cancelar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="pagination">
            <button disabled={page===0} onClick={()=>setPage(p=>p-1)}>← Anterior</button>
            <span>Página {page+1} de {totalPages}</span>
            <button disabled={page+1>=totalPages} onClick={()=>setPage(p=>p+1)}>Seguinte →</button>
          </div>
        </div>
      )}
    </div>
  )
}

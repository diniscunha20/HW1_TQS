import {useState} from 'react'
import MunicipalitySelect from '../components/MunicipalitySelect'
import {createBooking, getByToken} from '../api'

export default function Cidadao(){
  const [form,setForm]=useState({name:'', municipalityCode:'', date:'', timeSlot:'AM', description:''})
  const [result,setResult]=useState(null)
  const [error,setError]=useState(null)
  const [token,setToken]=useState('')
  const [status,setStatus]=useState(null)
  const [searchError,setSearchError]=useState(null)
  
  const onSubmit=async e=>{ 
    e.preventDefault()
    setError(null)
    setResult(null)
    try {
      const response = await createBooking(form)
      setResult(response)
    } catch (err) {
      setError(err.message || 'Erro ao criar marcação')
    }
  }
  
  const onCheck=async ()=>{ 
    setSearchError(null)
    setStatus(null)
    try {
      const response = await getByToken(token)
      setStatus(response)
    } catch (err) {
      setSearchError(err.message || 'Token não encontrado')
    }
  }
  
  return (
    <div>
      <div className="card">
        <h2>Novo Pedido de Recolha</h2>
        <form onSubmit={onSubmit}>
          <div className="form-group">
            <label>Nome Completo</label>
            <input 
              placeholder="Ex: João Silva" 
              value={form.name} 
              onChange={e=>setForm({...form,name:e.target.value})}
              required
            />
          </div>
          
          <div className="form-group">
            <label>Município</label>
            <MunicipalitySelect 
              value={form.municipalityCode} 
              onChange={v=>setForm({...form, municipalityCode:v})}
            />
          </div>
          
          <div className="form-group">
            <label>Data de Recolha</label>
            <input 
              type="date" 
              value={form.date} 
              onChange={e=>setForm({...form,date:e.target.value})}
              required
            />
          </div>
          
          <div className="form-group">
            <label>Período</label>
            <select value={form.timeSlot} onChange={e=>setForm({...form,timeSlot:e.target.value})}>
              <option value="AM">Manhã (AM)</option>
              <option value="PM">Tarde (PM)</option>
            </select>
          </div>
          
          <div className="form-group">
            <label>Descrição / Observações</label>
            <textarea 
              placeholder="Descreva os monos a recolher..."
              value={form.description}
              onChange={e=>setForm({...form,description:e.target.value})}
              rows="3"
              required
            />
          </div>
          
          <button type="submit" className="btn-primary">Submeter Pedido</button>
        </form>
        
        {error && <div className="error-message">❌ {error}</div>}
        
        {result && (
          <div className="token-display">
            <p>✅ Pedido criado com sucesso!</p>
            <p>Guarde o seu token para consultar o estado:</p>
            <p>Token: <strong>{result.token}</strong></p>
            <p>Estado: <span className={`status-badge status-${result.status?.toLowerCase()}`}>{result.status}</span></p>
          </div>
        )}
      </div>

      <div className="card">
        <h3>🔍 Consultar Estado por Token</h3>
        <div style={{display: 'flex', gap: '8px'}}>
          <input 
            placeholder="Ex: ABCD1234" 
            value={token} 
            onChange={e=>setToken(e.target.value.toUpperCase())}
            style={{flex: 1}}
          />
          <button onClick={onCheck} className="btn-secondary">Consultar</button>
        </div>
        
        {searchError && <div className="error-message">❌ {searchError}</div>}
        
        {status && (
          <div className="token-display">
            <p><strong>Nome:</strong> {status.name}</p>
            <p><strong>Município:</strong> {status.municipalityCode}</p>
            <p><strong>Data:</strong> {status.date}</p>
            <p><strong>Período:</strong> {status.timeSlot}</p>
            <p><strong>Descrição:</strong> {status.description}</p>
            <p><strong>Estado:</strong> <span className={`status-badge status-${status.status?.toLowerCase()}`}>{status.status}</span></p>
          </div>
        )}
      </div>
    </div>
  )
}

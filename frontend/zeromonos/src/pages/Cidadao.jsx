import {useState} from 'react'
import MunicipalitySelect from '../components/MunicipalitySelect'
import {createBooking, getByToken} from '../api'

export default function Cidadao(){
  const [form,setForm]=useState({citizenName:'', municipality:'', pickupDate:'', timeSlot:'AM'})
  const [result,setResult]=useState(null); const [token,setToken]=useState(''); const [status,setStatus]=useState(null)
  const onSubmit=async e=>{ e.preventDefault(); setResult(await createBooking(form)) }
  const onCheck=async ()=>{ setStatus(await getByToken(token)) }
  return (
    <div>
      <h2>Novo pedido</h2>
      <form onSubmit={onSubmit}>
        <input placeholder="Nome" value={form.citizenName} onChange={e=>setForm({...form,citizenName:e.target.value})}/>
        <MunicipalitySelect value={form.municipality} onChange={v=>setForm({...form, municipality:v})}/>
        <input type="date" value={form.pickupDate} onChange={e=>setForm({...form,pickupDate:e.target.value})}/>
        <select value={form.timeSlot} onChange={e=>setForm({...form,timeSlot:e.target.value})}>
          <option>AM</option><option>PM</option>
        </select>
        <button type="submit">Submeter</button>
      </form>
      {result && <p>Token: <strong>{result.token}</strong> | Estado: {result.status}</p>}

      <h3>Consultar por token</h3>
      <input placeholder="ABCD1234" value={token} onChange={e=>setToken(e.target.value)}/>
      <button onClick={onCheck}>Consultar</button>
      {status && <pre>{JSON.stringify(status,null,2)}</pre>}
    </div>
  )
}

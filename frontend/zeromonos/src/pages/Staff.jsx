import {useEffect, useState} from 'react'
import MunicipalitySelect from '../components/MunicipalitySelect'
import {listBookings, updateStatus} from '../api'

export default function Staff(){
  const [municipality,setMunicipality]=useState(''); const [status,setStatus]=useState('PENDING')
  const [page,setPage]=useState(0); const [data,setData]=useState({items:[], total:0})
  useEffect(()=>{ if(!municipality) return; listBookings({municipality,status,page}).then(setData) },[municipality,status,page])
  const set = (id, s)=> updateStatus(id,s).then(()=> listBookings({municipality,status,page}).then(setData))
  return (
    <div>
      <h2>Pedidos por município</h2>
      <MunicipalitySelect value={municipality} onChange={setMunicipality}/>
      <select value={status} onChange={e=>{setPage(0); setStatus(e.target.value)}}>
        <option>PENDING</option><option>CONFIRMED</option><option>IN_PROGRESS</option><option>DONE</option><option>CANCELLED</option>
      </select>
      <table>
        <thead><tr><th>Data</th><th>Slot</th><th>Token</th><th>Status</th><th>Ações</th></tr></thead>
        <tbody>
          {data.items.map(it=>(
            <tr key={it.id}>
              <td>{it.pickupDate}</td><td>{it.timeSlot}</td><td>{it.token}</td><td>{it.status}</td>
              <td>
                <button onClick={()=>set(it.id,'CONFIRMED')}>Confirmar</button>
                <button onClick={()=>set(it.id,'IN_PROGRESS')}>Em curso</button>
                <button onClick={()=>set(it.id,'DONE')}>Concluir</button>
                <button onClick={()=>set(it.id,'CANCELLED')}>Cancelar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div>
        <button disabled={page===0} onClick={()=>setPage(p=>p-1)}>Anterior</button>
        <span> Página {page+1} </span>
        <button disabled={(page+1)*20>=data.total} onClick={()=>setPage(p=>p+1)}>Seguinte</button>
      </div>
    </div>
  )
}

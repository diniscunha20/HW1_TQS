import {useEffect, useState} from 'react'
import {fetchMunicipalities} from '../api'

export default function MunicipalitySelect({value, onChange}) {
  const [q,setQ] = useState(''); const [items,setItems]=useState([]); const [loading,setLoading]=useState(false)
  useEffect(()=>{ let alive=true; setLoading(true);
    fetchMunicipalities(q).then(d=>{ if(alive) setItems(d)}).finally(()=>alive&&setLoading(false))
    return ()=>{alive=false}
  },[q])
  return (
    <div>
      <input placeholder="Procurar município…" value={q} onChange={e=>setQ(e.target.value)} />
      <select value={value||''} onChange={e=>onChange(e.target.value)}>
        <option value="" disabled>— selecione —</option>
        {items.map(m => <option key={m.code||m.name} value={m.name}>{m.name}</option>)}
      </select>
      {loading && <small>a carregar…</small>}
    </div>
  )
}

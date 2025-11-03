import {useState} from 'react'
import Cidadao from './pages/Cidadao'
import Staff from './pages/Staff'
import './App.css'

export default function App(){
  const [tab,setTab]=useState('cidadao')
  const btnStyle = (active) => ({
    padding: '6px 10px',
    fontSize: 14,
    borderRadius: 6,
    border: '1px solid #d1d5db',
    background: active ? '#111827' : '#f3f4f6',
    color: active ? '#fff' : '#111827',
    cursor: 'pointer'
  })

  return (
    <>
      <header style={{display:'flex',alignItems:'center',justifyContent:'space-between',padding:'6px 12px', borderRadius: 8,borderBottom:'1px solid #e5e7eb',background:'#fff'}}>
        <span style={{fontSize:16,fontWeight:600}}>Recolha de Monos</span>
        <nav>
          <button
            style={btnStyle(tab === 'cidadao')}
            onClick={()=>setTab('cidadao')}
          >
            Cidadão
          </button>
          <button
            style={btnStyle(tab === 'staff')}
            onClick={()=>setTab('staff')}
          >
            Staff
          </button>
        </nav>
      </header>
      <main style={{padding:12}}>
        {tab==='cidadao'? <Cidadao/> : <Staff/>}
      </main>
    </>
  )
}

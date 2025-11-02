import {useState} from 'react'
import Cidadao from './pages/Cidadao'
import Staff from './pages/Staff'
export default function App(){
  const [tab,setTab]=useState('cidadao')
  return (
    <div style={{padding:16}}>
      <h1>Recolha de Monos</h1>
      <nav>
        <button onClick={()=>setTab('cidadao')}>Cidadão</button>
        <button onClick={()=>setTab('staff')}>Staff</button>
      </nav>
      {tab==='cidadao'? <Cidadao/> : <Staff/>}
    </div>
  )
}

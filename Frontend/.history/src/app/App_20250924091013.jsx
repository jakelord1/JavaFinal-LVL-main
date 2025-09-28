import { BrowserRouter } from 'react-router-dom'
import './ui/App.css'

export default function App() {
  return <BrowserRouter>
    <Routes>
      <Route path='/' element={<Layout />}>
        <Route index element={<Home />} />
        <Route path='privacy' element={<Privacy />} />
      </Route>
    </Routes>
  </BrowserRouter>;
}


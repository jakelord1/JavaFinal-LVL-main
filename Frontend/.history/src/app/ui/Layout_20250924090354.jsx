import { Outlet } from "react-router-dom";

export default function Layout({ children }) {
  return <>
    <header>Header</header>
    <main><Outlet/></main>
    <footer></footer>
  </>
}
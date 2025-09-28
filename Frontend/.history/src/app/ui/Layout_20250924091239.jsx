import { Outlet } from "react-router-dom";
import './Layout.css';

export default function Layout() {
    return <>
        <header>Header</header>
        <main><Outlet /></main>
        <footer>Footer</footer>
    </>;
}
/**
 * 애플리케이션 루트 컴포넌트
 * 
 * 라우팅 설정 및 전체 애플리케이션 구조를 정의합니다.
 */

import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Layout } from './components/layout/Layout';
import { DashboardPage } from './pages/DashboardPage';
import { QuestionsPage } from './pages/QuestionsPage';
import { RecordsPage } from './pages/RecordsPage';
import { UsersPage } from './pages/UsersPage';

/**
 * App 컴포넌트
 */
function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/questions" element={<QuestionsPage />} />
          <Route path="/records" element={<RecordsPage />} />
          <Route path="/users" element={<UsersPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;

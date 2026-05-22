import { useEffect, useState } from 'react'
import { api } from './services/api'
import './App.css'

const statusOptions = ['RECEBIDO', 'EM_PREPARO', 'PRONTO', 'ENTREGUE', 'CANCELADO']

const initialCliente = {
  nome: '',
  email: '',
  telefone: '',
}

const initialPedido = {
  clienteId: '',
  item: '',
  observacao: '',
  valorTotal: '',
}

function App() {
  const [activeTab, setActiveTab] = useState('clientes')
  const [clientes, setClientes] = useState([])
  const [pedidos, setPedidos] = useState([])
  const [pedidosCozinha, setPedidosCozinha] = useState([])
  const [historico, setHistorico] = useState([])
  const [clienteForm, setClienteForm] = useState(initialCliente)
  const [pedidoForm, setPedidoForm] = useState(initialPedido)
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('Sistema pronto para cadastrar clientes e pedidos.')

  const carregarDados = async () => {
    setLoading(true)
    try {
      const [clientesRes, pedidosRes, cozinhaRes, historicoRes] = await Promise.all([
        api.get('/clientes'),
        api.get('/pedidos'),
        api.get('/pedidos/cozinha'),
        api.get('/historico'),
      ])

      setClientes(clientesRes.data)
      setPedidos(pedidosRes.data)
      setPedidosCozinha(cozinhaRes.data)
      setHistorico(historicoRes.data)
    } catch (error) {
      setMessage(getErrorMessage(error, 'Nao foi possivel carregar os dados da API.'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    carregarDados()
  }, [])

  const criarCliente = async (event) => {
    event.preventDefault()
    try {
      await api.post('/clientes', clienteForm)
      setClienteForm(initialCliente)
      setMessage('Cliente cadastrado com sucesso.')
      await carregarDados()
    } catch (error) {
      setMessage(getErrorMessage(error, 'Erro ao cadastrar cliente.'))
    }
  }

  const removerCliente = async (id) => {
    try {
      await api.delete(`/clientes/${id}`)
      setMessage('Cliente removido com sucesso.')
      await carregarDados()
    } catch (error) {
      setMessage(getErrorMessage(error, 'Nao foi possivel remover o cliente.'))
    }
  }

  const criarPedido = async (event) => {
    event.preventDefault()
    try {
      await api.post('/pedidos', {
        ...pedidoForm,
        clienteId: Number(pedidoForm.clienteId),
        valorTotal: Number(pedidoForm.valorTotal),
      })
      setPedidoForm(initialPedido)
      setMessage('Pedido criado e enviado para notificacao.')
      await carregarDados()
    } catch (error) {
      setMessage(getErrorMessage(error, 'Erro ao criar pedido.'))
    }
  }

  const atualizarStatus = async (pedidoId, status) => {
    try {
      await api.patch(`/pedidos/${pedidoId}/status`, { status })
      setMessage(`Pedido #${pedidoId} atualizado para ${formatStatus(status)}.`)
      await carregarDados()
    } catch (error) {
      setMessage(getErrorMessage(error, 'Erro ao atualizar status do pedido.'))
    }
  }

  const resumo = [
    { label: 'Clientes cadastrados', value: clientes.length },
    { label: 'Pedidos totais', value: pedidos.length },
    { label: 'Fila da cozinha', value: pedidosCozinha.length },
    { label: 'Eventos no historico', value: historico.length },
  ]

  const tabs = [
    { id: 'clientes', label: 'Tela clientes' },
    { id: 'pedidos', label: 'Tela pedidos' },
    { id: 'cozinha', label: 'Tela cozinha' },
  ]

  return (
    <main className="app-shell">
      <section className="hero-panel">
        <div className="eyebrow">Lanchonete</div>
        <div className="hero-copy">
          <div>
            <h1>Lanchonete Digas & Cia</h1>
           
          </div>
          <div className="status-card">
            <span className="pulse" />
            <strong>{loading ? 'Sincronizando com a API...' : 'API conectada'}</strong>
            <small>{message}</small>
          </div>
        </div>
        <div className="summary-grid">
          {resumo.map((item) => (
            <article key={item.label} className="summary-card">
              <span>{item.label}</span>
              <strong>{item.value}</strong>
            </article>
          ))}
        </div>
      </section>

      <nav className="tab-bar" aria-label="Navegacao principal">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            type="button"
            className={activeTab === tab.id ? 'tab active' : 'tab'}
            onClick={() => setActiveTab(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </nav>

      <section className="content-grid">
        <div className="workspace">
          {activeTab === 'clientes' && (
            <section className="panel">
              <div className="panel-head">
                <h2>Cadastro de clientes</h2>
                <p>Crie e remova clientes que vao receber atualizacao de pedidos por e-mail.</p>
              </div>

              <form className="form-grid" onSubmit={criarCliente}>
                <input
                  placeholder="Nome do cliente"
                  value={clienteForm.nome}
                  onChange={(event) => setClienteForm({ ...clienteForm, nome: event.target.value })}
                />
                <input
                  type="email"
                  placeholder="email@cliente.com"
                  value={clienteForm.email}
                  onChange={(event) => setClienteForm({ ...clienteForm, email: event.target.value })}
                />
                <input
                  placeholder="Telefone"
                  value={clienteForm.telefone}
                  onChange={(event) => setClienteForm({ ...clienteForm, telefone: event.target.value })}
                />
                <button type="submit" className="primary-button">Cadastrar cliente</button>
              </form>

              <div className="list-grid">
                {clientes.map((cliente) => (
                  <article key={cliente.id} className="list-card">
                    <div>
                      <h3>{cliente.nome}</h3>
                      <p>{cliente.email}</p>
                      <small>{cliente.telefone}</small>
                    </div>
                    <button type="button" className="ghost-button" onClick={() => removerCliente(cliente.id)}>
                      Remover
                    </button>
                  </article>
                ))}
                {clientes.length === 0 && <EmptyState text="Nenhum cliente cadastrado ainda." />}
              </div>
            </section>
          )}

          {activeTab === 'pedidos' && (
            <section className="panel">
              <div className="panel-head">
                <h2>Gestao de pedidos</h2>
                <p>Escolha um cliente, registre o item e envie o pedido para a fila do sistema.</p>
              </div>

              <form className="form-grid" onSubmit={criarPedido}>
                <select
                  value={pedidoForm.clienteId}
                  onChange={(event) => setPedidoForm({ ...pedidoForm, clienteId: event.target.value })}
                >
                  <option value="">Selecione o cliente</option>
                  {clientes.map((cliente) => (
                    <option key={cliente.id} value={cliente.id}>
                      {cliente.nome}
                    </option>
                  ))}
                </select>
                <input
                  placeholder="Item do pedido"
                  value={pedidoForm.item}
                  onChange={(event) => setPedidoForm({ ...pedidoForm, item: event.target.value })}
                />
                <input
                  placeholder="Observacao"
                  value={pedidoForm.observacao}
                  onChange={(event) => setPedidoForm({ ...pedidoForm, observacao: event.target.value })}
                />
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="Valor total"
                  value={pedidoForm.valorTotal}
                  onChange={(event) => setPedidoForm({ ...pedidoForm, valorTotal: event.target.value })}
                />
                <button type="submit" className="primary-button">Criar pedido</button>
              </form>

              <div className="table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Cliente</th>
                      <th>Item</th>
                      <th>Status</th>
                      <th>Valor</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pedidos.map((pedido) => (
                      <tr key={pedido.id}>
                        <td>#{pedido.id}</td>
                        <td>{pedido.clienteNome}</td>
                        <td>{pedido.item}</td>
                        <td>
                          <span className="status-pill">{formatStatus(pedido.status)}</span>
                        </td>
                        <td>R$ {Number(pedido.valorTotal).toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                {pedidos.length === 0 && <EmptyState text="Nenhum pedido criado ainda." />}
              </div>
            </section>
          )}

          {activeTab === 'cozinha' && (
            <section className="panel">
              <div className="panel-head">
                <h2>Painel da cozinha</h2>
                <p>Atualize o status do preparo e dispare o consumer de notificacao com historico e e-mail.</p>
              </div>

              <div className="kitchen-grid">
                {pedidosCozinha.map((pedido) => (
                  <article key={pedido.id} className="kitchen-card">
                    <div className="kitchen-card-top">
                      <span>Pedido #{pedido.id}</span>
                      <span className="status-pill">{formatStatus(pedido.status)}</span>
                    </div>
                    <h3>{pedido.item}</h3>
                    <p>{pedido.clienteNome}</p>
                    <small>{pedido.observacao}</small>
                    <div className="status-actions">
                      {statusOptions.map((status) => (
                        <button
                          key={status}
                          type="button"
                          className={pedido.status === status ? 'status-button selected' : 'status-button'}
                          onClick={() => atualizarStatus(pedido.id, status)}
                        >
                          {formatStatus(status)}
                        </button>
                      ))}
                    </div>
                  </article>
                ))}
                {pedidosCozinha.length === 0 && <EmptyState text="A cozinha nao possui pedidos ativos." />}
              </div>
            </section>
          )}
        </div>

        <aside className="sidebar">
          <section className="panel">
            <div className="panel-head compact">
              <h2>Historico recente</h2>
              <p>Eventos consumidos pela fila de notificacao.</p>
            </div>
            <div className="timeline">
              {historico.map((evento) => (
                <article key={evento.id} className="timeline-item">
                  <strong>Pedido #{evento.pedidoId}</strong>
                  <span>{formatStatus(evento.status)}</span>
                  <p>{evento.mensagem}</p>
                  <small>{evento.clienteEmail}</small>
                </article>
              ))}
              {historico.length === 0 && <EmptyState text="Nenhum evento consumido ainda." />}
            </div>
          </section>
        </aside>
      </section>
    </main>
  )
}

function EmptyState({ text }) {
  return <div className="empty-state">{text}</div>
}

function formatStatus(status) {
  return status.toLowerCase().replaceAll('_', ' ')
}

function getErrorMessage(error, fallback) {
  return error?.response?.data?.message || fallback
}

export default App

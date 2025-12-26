"use client";

import { useMarketData } from "@/hooks/useMarketData";
import { PriceCard } from "@/components/PriceCard";

export default function Home() {
  const { prices, isConnected } = useMarketData();
  const sortedSymbols = Object.keys(prices).sort();

  return (
    <main className="min-h-screen bg-slate-950 text-white p-8">
      {/* Header */}
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold bg-gradient-to-r from-blue-400 to-cyan-400 bg-clip-text text-transparent">
            StockSim <span className="text-slate-500 font-light">Pro</span>
          </h1>
          <p className="text-slate-400">High-Frequency Paper Trading</p>
        </div>
        <div className="flex items-center gap-2">
          <div className={`w-3 h-3 rounded-full ${isConnected ? 'bg-green-500 animate-pulse' : 'bg-red-500'}`} />
          <span className="text-sm text-slate-400">{isConnected ? 'Live Feed' : 'Disconnected'}</span>
        </div>
      </div>

      {/* Market Ticker Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {sortedSymbols.length === 0 ? (
          [1, 2, 3, 4].map((i) => (
            <div key={i} className="h-24 bg-slate-900 rounded-xl animate-pulse" />
          ))
        ) : (
          sortedSymbols.map((symbol) => (
            <PriceCard key={symbol} symbol={symbol} price={prices[symbol].price} />
          ))
        )}
      </div>

      {/* Main Trading Area Placeholder */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 bg-slate-900/50 rounded-2xl p-6 border border-slate-800 h-[400px] flex items-center justify-center text-slate-500">
          Chart Area (Recharts Comming Soon)
        </div>
        <div className="bg-slate-900/50 rounded-2xl p-6 border border-slate-800">
          <h2 className="text-xl font-bold mb-4">Place Order</h2>
          <div className="space-y-4">
            <input type="text" placeholder="Symbol (e.g. AAPL)" className="w-full bg-slate-800 border border-slate-700 p-3 rounded-lg text-white" />
            <div className="flex gap-2">
              <button className="flex-1 bg-green-500 hover:bg-green-600 text-black font-bold py-3 rounded-lg">BUY</button>
              <button className="flex-1 bg-red-500 hover:bg-red-600 text-white font-bold py-3 rounded-lg">SELL</button>
            </div>
          </div>
        </div>
      </div>

    </main>
  );
}

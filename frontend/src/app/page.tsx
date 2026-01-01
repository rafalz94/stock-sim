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
            StockSim <span className="text-slate-500 font-light">Live</span>
          </h1>
          <p className="text-slate-400">Real-time Market Monitor</p>
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
      {/* Main Trading Area Placeholder */}
      <div className="w-full">
        <div className="bg-slate-900/50 rounded-2xl p-6 border border-slate-800 h-[600px] flex items-center justify-center text-slate-500">
          <div className="text-center">
            <h3 className="text-2xl font-bold text-slate-300 mb-2">Live Market Visualization</h3>
            <p>Real-time chart integration coming soon...</p>
          </div>
        </div>
      </div>

    </main>
  );
}

import { useState } from "react";
import Hero from "@/components/Hero";
import PluginCreator from "@/components/PluginCreator";

const Index = () => {
  const [showCreator, setShowCreator] = useState(false);

  return (
    <div className="min-h-screen">
      {!showCreator ? (
        <Hero onGetStarted={() => setShowCreator(true)} />
      ) : (
        <PluginCreator />
      )}
    </div>
  );
};

export default Index;

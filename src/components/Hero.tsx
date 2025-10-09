import { Button } from "@/components/ui/button";
import { Sparkles, Zap } from "lucide-react";
import heroBg from "@/assets/hero-bg.jpg";

interface HeroProps {
  onGetStarted: () => void;
}

const Hero = ({ onGetStarted }: HeroProps) => {
  return (
    <div className="relative min-h-screen flex items-center justify-center overflow-hidden">
      {/* Background Image with Overlay */}
      <div 
        className="absolute inset-0 bg-cover bg-center"
        style={{ backgroundImage: `url(${heroBg})` }}
      >
        <div className="absolute inset-0 bg-background/80 backdrop-blur-sm" />
      </div>

      {/* Content */}
      <div className="relative z-10 container mx-auto px-4 text-center animate-fade-in">
        <div className="inline-block mb-6 px-4 py-2 bg-card/50 backdrop-blur-md rounded-full border border-primary/20">
          <span className="text-sm text-muted-foreground">Powered by Google Gemini 2.5 Pro</span>
        </div>

        <h1 className="text-5xl md:text-7xl font-bold mb-6 bg-gradient-primary bg-clip-text text-transparent">
          StormPluginMaker
        </h1>

        <p className="text-xl md:text-2xl text-muted-foreground mb-8 max-w-3xl mx-auto">
          Generate, compile, and deploy plugins in seconds with the power of advanced AI
        </p>

        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
          <Button 
            size="lg" 
            onClick={onGetStarted}
            className="group relative px-8 py-6 text-lg bg-primary hover:bg-primary/90 shadow-glow transition-all duration-300 hover:shadow-glow-secondary"
          >
            <Sparkles className="mr-2 h-5 w-5 group-hover:animate-pulse" />
            Start Creating
          </Button>

          <Button 
            size="lg" 
            variant="outline"
            className="px-8 py-6 text-lg border-primary/30 hover:bg-primary/10 hover:border-primary/50 transition-all duration-300"
          >
            <Zap className="mr-2 h-5 w-5" />
            Learn More
          </Button>
        </div>

        {/* Feature Pills */}
        <div className="mt-16 flex flex-wrap justify-center gap-4">
          {['AI-Powered Generation', 'Real-Time Compilation', 'Instant Deployment'].map((feature) => (
            <div 
              key={feature}
              className="px-6 py-3 bg-card/30 backdrop-blur-md rounded-full border border-primary/10 text-sm hover:border-primary/30 transition-all duration-300"
            >
              {feature}
            </div>
          ))}
        </div>
      </div>

      {/* Floating Elements */}
      <div className="absolute top-20 left-10 w-20 h-20 bg-primary/20 rounded-full blur-3xl animate-float" />
      <div className="absolute bottom-20 right-10 w-32 h-32 bg-secondary/20 rounded-full blur-3xl animate-float" style={{ animationDelay: '2s' }} />
    </div>
  );
};

export default Hero;

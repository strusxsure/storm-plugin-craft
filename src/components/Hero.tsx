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
      <div className="relative z-10 container mx-auto px-4 sm:px-6 lg:px-8 text-center animate-fade-in py-8">
        <div className="inline-block mb-4 sm:mb-6 px-3 sm:px-4 py-2 bg-card/50 backdrop-blur-md rounded-full border border-primary/20">
          <span className="text-xs sm:text-sm text-muted-foreground">Powered by Google Gemini 2.5 Pro</span>
        </div>

        <h1 className="text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-bold mb-4 sm:mb-6 bg-gradient-primary bg-clip-text text-transparent px-2">
          StormPluginMaker
        </h1>

        <p className="text-base sm:text-lg md:text-xl lg:text-2xl text-muted-foreground mb-6 sm:mb-8 max-w-3xl mx-auto px-4">
          Generate, compile, and deploy plugins in seconds with the power of advanced AI
        </p>

        <div className="flex flex-col sm:flex-row gap-3 sm:gap-4 justify-center items-stretch sm:items-center px-4">
          <Button 
            size="lg" 
            onClick={onGetStarted}
            className="group relative px-6 sm:px-8 py-5 sm:py-6 text-base sm:text-lg bg-primary hover:bg-primary/90 shadow-glow transition-all duration-300 hover:shadow-glow-secondary w-full sm:w-auto"
          >
            <Sparkles className="mr-2 h-4 w-4 sm:h-5 sm:w-5 group-hover:animate-pulse" />
            Start Creating
          </Button>

          <Button 
            size="lg" 
            variant="outline"
            className="px-6 sm:px-8 py-5 sm:py-6 text-base sm:text-lg border-primary/30 hover:bg-primary/10 hover:border-primary/50 transition-all duration-300 w-full sm:w-auto"
          >
            <Zap className="mr-2 h-4 w-4 sm:h-5 sm:w-5" />
            Learn More
          </Button>
        </div>

        {/* Feature Pills */}
        <div className="mt-8 sm:mt-12 lg:mt-16 flex flex-wrap justify-center gap-2 sm:gap-4 px-4">
          {['AI-Powered Generation', 'Real-Time Compilation', 'Instant Deployment'].map((feature) => (
            <div 
              key={feature}
              className="px-3 sm:px-6 py-2 sm:py-3 bg-card/30 backdrop-blur-md rounded-full border border-primary/10 text-xs sm:text-sm hover:border-primary/30 transition-all duration-300"
            >
              {feature}
            </div>
          ))}
        </div>
      </div>

      {/* Floating Elements - Hidden on mobile */}
      <div className="hidden sm:block absolute top-20 left-10 w-20 h-20 bg-primary/20 rounded-full blur-3xl animate-float" />
      <div className="hidden sm:block absolute bottom-20 right-10 w-32 h-32 bg-secondary/20 rounded-full blur-3xl animate-float" style={{ animationDelay: '2s' }} />
    </div>
  );
};

export default Hero;

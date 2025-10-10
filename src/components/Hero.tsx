import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Sparkles, Zap, LogOut } from "lucide-react";
import { supabase } from "@/integrations/supabase/client";
import { useToast } from "@/hooks/use-toast";
import type { User } from "@supabase/supabase-js";
import heroBg from "@/assets/hero-bg.jpg";

interface HeroProps {
  onGetStarted: () => void;
}

const Hero = ({ onGetStarted }: HeroProps) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast();

  useEffect(() => {
    // Check current session
    supabase.auth.getSession().then(({ data: { session } }) => {
      setUser(session?.user ?? null);
    });

    // Listen for auth changes
    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, session) => {
      setUser(session?.user ?? null);
    });

    return () => subscription.unsubscribe();
  }, []);

  const handleGoogleSignIn = async () => {
    setIsLoading(true);
    try {
      const { error } = await supabase.auth.signInWithOAuth({
        provider: 'google',
        options: {
          redirectTo: `${window.location.origin}/`,
        },
      });

      if (error) throw error;
    } catch (error: any) {
      toast({
        title: "Authentication Error",
        description: error.message || "Failed to sign in with Google",
        variant: "destructive",
      });
      setIsLoading(false);
    }
  };

  const handleSignOut = async () => {
    try {
      const { error } = await supabase.auth.signOut();
      if (error) throw error;
      
      toast({
        title: "Signed Out",
        description: "You have been successfully signed out",
      });
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.message || "Failed to sign out",
        variant: "destructive",
      });
    }
  };

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
        {/* User Status */}
        {user && (
          <div className="absolute top-4 right-4 flex items-center gap-3 bg-card/70 backdrop-blur-md px-4 py-2 rounded-full border border-primary/20">
            <span className="text-xs sm:text-sm text-muted-foreground">
              {user.email}
            </span>
            <Button
              size="sm"
              variant="ghost"
              onClick={handleSignOut}
              className="h-7 px-2"
            >
              <LogOut className="h-3 w-3" />
            </Button>
          </div>
        )}

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

          {!user && (
            <Button 
              size="lg" 
              onClick={handleGoogleSignIn}
              disabled={isLoading}
              className="px-6 sm:px-8 py-5 sm:py-6 text-base sm:text-lg bg-white text-gray-900 hover:bg-gray-100 shadow-elegant transition-all duration-300 w-full sm:w-auto"
            >
              <svg className="mr-2 h-5 w-5" viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              {isLoading ? "Signing in..." : "Sign in with Google"}
            </Button>
          )}

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

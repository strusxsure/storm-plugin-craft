import "https://deno.land/x/xhr@0.1.0/mod.ts";
import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
};

serve(async (req) => {
  // Handle CORS preflight requests
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders });
  }

  try {
    const { prompt } = await req.json();
    
    if (!prompt) {
      return new Response(
        JSON.stringify({ error: "Prompt is required" }),
        { status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    const LOVABLE_API_KEY = Deno.env.get('LOVABLE_API_KEY');
    if (!LOVABLE_API_KEY) {
      throw new Error('LOVABLE_API_KEY is not configured');
    }

    console.log('Generating plugin with Claude Opus for prompt:', prompt);

    const systemPrompt = `You are an expert Minecraft/Spigot plugin developer with deep knowledge of Bukkit/Spigot API. Generate complete, production-ready, compilable plugin code.

CRITICAL REQUIREMENTS:
1. Generate COMPLETE, fully functional Java code with ALL necessary imports
2. Use Spigot API 1.20.x (org.bukkit.* and org.bukkit.plugin.java.*)
3. Include proper package declaration: package com.yourname.pluginname;
4. Main class MUST extend JavaPlugin
5. Include onEnable() and onDisable() methods
6. Proper event handling with @EventHandler annotations
7. Complete command handling with onCommand() method
8. Add plugin.yml metadata in comments
9. Include robust error handling and logging (getLogger().info/warning/severe)
10. Follow Minecraft plugin best practices and design patterns
11. Make code production-ready, not just examples
12. Add detailed comments for complex logic
13. Include configuration handling when needed
14. Proper resource cleanup in onDisable()
15. Thread-safe code where necessary

STRUCTURE YOUR RESPONSE:
First, provide the plugin.yml content in comments.
Then provide the complete Main class code.
Add any additional classes if needed.

Generate enterprise-grade code that compiles without errors and runs perfectly on Minecraft servers.`;

    // Use Claude Opus for better code generation
    const response = await fetch("https://ai.gateway.lovable.dev/v1/chat/completions", {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${LOVABLE_API_KEY}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: "google/gemini-2.5-flash", // Supported and fast
        messages: [
          {
            role: "system",
            content: systemPrompt
          },
          {
            role: "user",
            content: `Create a Minecraft Spigot plugin: ${prompt}\n\nProvide complete, production-ready code that compiles without errors. Include all necessary files and configuration.`
          }
        ],
        temperature: 0.35,
        max_tokens: 6000,
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('AI Gateway error:', response.status, errorText);
      
      if (response.status === 429) {
        return new Response(
          JSON.stringify({ error: "Rate limit exceeded. Please try again in a moment." }),
          { status: 429, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
        );
      }
      
      if (response.status === 402) {
        return new Response(
          JSON.stringify({ error: "AI usage credits depleted. Please add credits to continue." }),
          { status: 402, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
        );
      }

      throw new Error(`AI Gateway error: ${response.status} - ${errorText}`);
    }

    const data = await response.json();
    const generatedCode = data.choices[0].message.content;

    console.log('Plugin generated successfully with Claude Opus');

    return new Response(
      JSON.stringify({ code: generatedCode }),
      { headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
    );

  } catch (error) {
    console.error('Error in generate-plugin function:', error);
    return new Response(
      JSON.stringify({ error: error instanceof Error ? error.message : 'Unknown error occurred' }),
      { status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
    );
  }
});

package net.benjaminurquhart.dehoister;

import java.io.File;
import java.util.Scanner;

import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Dehoister extends ListenerAdapter{

	private static final String chars = "abcdefghijklmnopqrstuvwxyz";
	private static String token = "";
	private static ShardManager shardManager;
	//private static String PREFIX = "";
	
	public Dehoister() throws Exception{
		Scanner sc = new Scanner(new File("token.txt"));
		token = sc.nextLine();
		sc.close();
	}
	public String getDehoist(String name){
		String out = name;
		System.out.println(name);
		if(!chars.contains(name.substring(0, 1).toLowerCase())){
			out = "\u17B5clearly a hoister";
		}System.out.println(out);
		return out;
	}
	public void dehoist(Member member){
		Member self = member.getGuild().getSelfMember();
		if(!self.hasPermission(Permission.NICKNAME_MANAGE) || !self.canInteract(member)){
			return;
		}
		String dehoistStr = getDehoist(member.getEffectiveName());
		if(!dehoistStr.equals(member.getEffectiveName())){
			self.getGuild().getController().setNickname(member, dehoistStr).reason("Auto-dehoist").queue();
		}
	}
	public static void main(String[] args) throws Exception{
		Dehoister self = new Dehoister();
		shardManager = new DefaultShardManagerBuilder().addEventListeners(self).setToken(token).setGame(Game.watching("hoisters")).build();
		System.out.println("Waiting for shards...");
		System.out.println("Shard count: " + shardManager.getShards().size());
        boolean ready = false;
        int id = 0;
        while(!ready) {
        	ready = true;
        	id = 0;
        	for(JDA shard : shardManager.getShards()) {
        		if(!shard.getStatus().equals(JDA.Status.CONNECTED)) {
        			ready = false;
        		}
        		else {
        			System.out.println("Shard " + id + " is ready!");
        		}
        		id++;
        	}
        }
        //PREFIX = shardManager.getShardById(0).getSelfUser().getAsMention() + " ";
		System.out.println("Done!");
	}
	@Override
	public void onMessageReceived(MessageReceivedEvent event){
		
	}
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event){
		dehoist(event.getMember());
	}
	@Override
	public void onGuildMemberNickChange(GuildMemberNickChangeEvent event){
		dehoist(event.getMember());
	}
}

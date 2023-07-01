# HackathonPack
This is my plugin pack with all my submissions for the [ProjectKorra](https://github.com/ProjectKorra/ProjectKorra) Sub Element Hackathon competition in 2023 which should be put in the `plugins` folder. 

[![Github release](https://img.shields.io/github/v/release/slokxo/HackathonPack)](https://github.com/slokxo/HackathonPack/releases)
![Size](https://img.shields.io/github/repo-size/slokxo/HackathonPack.svg)
![Last commit](https://img.shields.io/github/last-commit/slokxo/HackathonPack.svg)

## About Hackathon Pack
> What sub elements are in it?

There are four sub elements, one of which is a custom sub element for Chi, called Techno. The other three are *Ice*, sub element of Water, *Sand*, sub element of Earth and *Metal*, sub element of Earth. I felt sand did not have a lot of representation throughout core, and well all addon ability making as there was __no__ core sand moves and very little addon moves. Techno is based off Book 1 Legend of Korra equalist chi-blocking with the high tech gadgets. 

## Abilities
### Sand
* QuickSand
  * QuickSand is an ability we see throughout the show, mostly by Bumi in his fight with Aang in the Omashu undergrounds.
* SandSlash
  * SandSlash is a standard combo used by sandenders (probably).. While we do not see it in the show, it is a very simple ability and if sandbenders can not do it.. well they're dumb! Just kidding, but it is very realistic and I think that they could do it.
### Ice
* IceGrip
  * IceGrip is a move that allows you to send out a cutting whip of ice and water, whoever gets hit with it will be encased in ic and eventually topple over! 
* IceDiscs
  * IceDiscs allows the player to aise a stump of ice and shoot a disc out of it. 
### Techno
* GrapplingHook
  * While GrapplingHook is not explicitly canon, it's been mentioned before. it allows the user to gain a grapple hook and wherever they shoot, they get pulled to it and the hook retracts! 
* EMPGrenade
  * EMPGrenade allows the user to send ou ta grenade of Electonic Magnetic Pulses that will disable any grappling hook gadgets within the area and damage. <-- Honestly, this wasn't canon, I was just bored but, you could argue that it could be made, taking into account Book 1's technological development. they had tasers, and they had smoke bombs so I can see open routes :)
### Metal
* MetalRun
  * MetalRun is like JedCore's WallRun, but you can shoot yourself off!

## Config Rundown
 I made this addon pack very configurable, in the subsequent message is the meaning behind all the values, what not to do, and the limitations.

## Sand
### Quicksand
* *Enabled* -> If the move is enabled or disabled.
* *Cooldown*  -> The delay in which you must wait before using the move again.
* *Range*  -> The range of the projection along the floor, if it exceeds its range or goes off a sand block, it will disperse.
* *Duration*  -> The duration in which the target will stay sinking.
* *Project Hitbox*  -> The hitbox that the projection will have. A high projection hitbox is not recommended.
* *Damage*  -> Damage that an entity will take in the quicksand (this is only enabled if the value `doDamage` is enabled.
* *Do Damage*  -> A true or false statement that decides whether the move does damage or not.

### SandSlash
* *Enabled* -> If the move is enabled or disabled.
* *Range* -> The range of the slash in the air, if it exceeds its range, it will disperse.
* *Damage* -> Damage that an entity will take if it is hit.
* *Speed* -> The speed the slash travels at.
* *Cooldown* -> The delay in which you must wait before using the move again.
* *Hit Radius* -> The hitbox that the slash will have.
* *Max Angle* -> The maximum angle it will have. Recommended to keep at base value.

## Ice
### IceDiscs
* *Enabled* -> If the move is enabled or disabled.
* *Cooldown* -> The delay in which you must wait before using the move again.
* *Range* -> The range of the disc in the air, if it exceeds its range, it will disperse.
* *Speed* -> The speed the disc travels at.
* *Select Range* -> The maximum range that you can source at.
* *Damage* -> Damage that an entity will take if it is hit.
* *Duration* -> The duration the stump will stay for.

### IceGrip
* *Enabled* -> If the move is enabled or disabled.
* *Cooldown* -> The delay in which you must wait before using the move again.
* *Select Range* -> The maximum range that you can source at.
* *Speed* -> The speed the water travels at.
* *Grip Duration* -> The duration a person will be affected by the grip.
* *Range* -> The range of the disc in the air, if it exceeds its range, it will retract.
* *Hitbox* -> The hitbox that the water will have. A high hitbox is not recommended.
* *Disperse Radius* -> The radius of blocks that will be affected by dispersion.
* *Controllable* -> If you can control it. CAUTION: This may break the move. You may have to switch slots to cancel it if it is stuck. It is severely recommended to not enable this.
* *Movement Handler* -> If the target is forced to stay still, like in paralyze.

## Techno
### Grappling Hook
* *Enabled* -> If the move is enabled or disabled.
* *Cooldown* -> The delay in which you must wait before using the move again.
* *Duration* -> The duration you have the bow for.
* *Range* -> The maximum range that you can shoot the bow at and move. This is denoted by the action bar, only accessible if `ShowBlocksInRange` is enabled.
* *Show Blocks In Range* -> Shows the blocks that you can shoot at.
* *ShiftSpeed* -> The amount of push you get on dismount.

### EMPGrenade
* *Enabled* -> If the move is enabled or disabled.
* *Cooldown* -> The delay in which you must wait before using the move again.
* *Duration* ->  The duration you have the grenade for.
* *Radius* -> The radius of particles.

### Metal
## MetalRun
* *Enabled* -> If the move is enabled or disabled.
* *Cooldown* -> The delay in which you must wait before using the move again.
* *Duration* ->  The duration you have the grenade for.
* *Speed* -> The speed you travel at.

## Commands
I have also made a few commands for this pack:
- `/b hackathonpack/hp/hackathon` will tell you a full list of them and their syntax.
- `/b hp reload` will reload the config for you, applying all edited values from the `config.yml` (found inside the HackathonPack) folder.
- `/b hp config` was originally made for testing purposes to see if my config actually worked but I decided to keep it since it could be useful. Beware though, it spams your chat with all the config values.
- `/actionb / actionbar / showhealth` will toggle a action bar to be sent to you. Inside that bar, will be the targeted entities health! (It will toggle when you use GrapplingHook)

## Additional Information
> Discord?

You can contact me on discord at `@slokx`, please do if you find any bugs with the code.
> Help

I would like to thank everyone that helped me in the Project Korra [development support](https://discord.com/channels/292809844803502080/612873490709741577) in the making of this pack, but more specifically, I would like to thank `@Aztl` and `@DreamerBoy` to help me make it. Without their help I wouldn't have made half of these moves.. <3 ty baes, and also thanks to `@MrHobo` for proving some of these moves canonicity with proof <3, As well as ProjectKorra's code.

Only EMPGrenade is not canon, the other *6* are canon and I will gladly debate so.

I also want to say, I do not expect to win, I am very new to the Spigot API, ProjectKorra and well, Java coding. I entered this to gain experience, and, experience is what I got it. Half of these moves are probably my most advanced move ever lol.. The Hackathon isn't about winning either, it's about providing moves for the community, and I did just that :)

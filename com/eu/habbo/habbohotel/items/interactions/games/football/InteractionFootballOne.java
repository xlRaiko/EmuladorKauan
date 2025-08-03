/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.games.football;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.football.FootballGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionPushable;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTeamItem;
import com.eu.habbo.habbohotel.items.interactions.games.football.goals.InteractionFootballGoal;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.ItemStateComposer;
import com.eu.habbo.util.pathfinding.Rotation;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFootballOne
extends InteractionPushable {
    public InteractionFootballOne(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionFootballOne(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public int getWalkOnVelocity(RoomUnit roomUnit, Room room) {
        if (roomUnit.getPath().isEmpty() && roomUnit.tilesWalked() >= 2 && roomUnit.tilesWalked() <= 3 && this.getExtradata().equals("1")) {
            return 0;
        }
        if (roomUnit.getPath().size() <= 1 && roomUnit.tilesWalked() >= 1) {
            return 1;
        }
        return 1;
    }

    @Override
    public int getWalkOffVelocity(RoomUnit roomUnit, Room room) {
        if (roomUnit.getPath().size() == 0 && roomUnit.tilesWalked() == 0) {
            return 1;
        }
        return 1;
    }

    @Override
    public int getDragVelocity(RoomUnit roomUnit, Room room) {
        if (roomUnit.getPath().isEmpty() && roomUnit.tilesWalked() == 2) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getTackleVelocity(RoomUnit roomUnit, Room room) {
        return 0;
    }

    @Override
    public RoomUserRotation getWalkOnDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public RoomUserRotation getWalkOffDirection(RoomUnit roomUnit, Room room) {
        RoomTile peek = roomUnit.getPath().peek();
        RoomTile nextWalkTile = peek != null ? room.getLayout().getTile(peek.x, peek.y) : roomUnit.getGoal();
        return RoomUserRotation.values()[(RoomUserRotation.values().length + Rotation.Calculate(roomUnit.getX(), roomUnit.getY(), nextWalkTile.x, nextWalkTile.y) + 4) % 8];
    }

    @Override
    public RoomUserRotation getDragDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public RoomUserRotation getTackleDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public int getNextRollDelay(int currentStep, int totalSteps) {
        if (totalSteps > 4 && currentStep <= 4) {
            return 125;
        }
        return 500;
    }

    @Override
    public RoomUserRotation getBounceDirection(Room room, RoomUserRotation currentDirection) {
        switch (currentDirection) {
            default: {
                return RoomUserRotation.SOUTH;
            }
            case NORTH_EAST: {
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_WEST.getValue()))) {
                    return RoomUserRotation.NORTH_WEST;
                }
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_EAST.getValue()))) {
                    return RoomUserRotation.SOUTH_EAST;
                }
                return RoomUserRotation.SOUTH_WEST;
            }
            case EAST: {
                return RoomUserRotation.WEST;
            }
            case SOUTH_EAST: {
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_WEST.getValue()))) {
                    return RoomUserRotation.SOUTH_WEST;
                }
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_EAST.getValue()))) {
                    return RoomUserRotation.NORTH_EAST;
                }
                return RoomUserRotation.NORTH_WEST;
            }
            case SOUTH: {
                return RoomUserRotation.NORTH;
            }
            case SOUTH_WEST: {
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_EAST.getValue()))) {
                    return RoomUserRotation.SOUTH_EAST;
                }
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_WEST.getValue()))) {
                    return RoomUserRotation.NORTH_WEST;
                }
                return RoomUserRotation.NORTH_EAST;
            }
            case WEST: {
                return RoomUserRotation.EAST;
            }
            case NORTH_WEST: 
        }
        if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_EAST.getValue()))) {
            return RoomUserRotation.NORTH_EAST;
        }
        if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_WEST.getValue()))) {
            return RoomUserRotation.SOUTH_WEST;
        }
        return RoomUserRotation.SOUTH_EAST;
    }

    @Override
    public boolean validMove(Room room, RoomTile from, RoomTile to) {
        BigDecimal ballHeight;
        if (to == null || to.state == RoomTileState.INVALID) {
            return false;
        }
        HabboItem topItem = room.getTopItemAt(to.x, to.y, this);
        if (topItem == null) {
            return true;
        }
        if (room.getItemsAt(to).stream().anyMatch(x -> !x.getBaseItem().allowStack())) {
            return false;
        }
        BigDecimal topItemHeight = BigDecimal.valueOf(topItem.getZ() + topItem.getBaseItem().getHeight());
        if (topItemHeight.subtract(ballHeight = BigDecimal.valueOf(this.getZ())).compareTo(new BigDecimal(1.65)) > 0) {
            return false;
        }
        if (topItem instanceof InteractionFootballGoal) {
            int ballDirection = Rotation.Calculate(from.x, from.y, to.x, to.y);
            int goalRotation = topItem.getRotation();
            switch (goalRotation) {
                case 0: {
                    return ballDirection > 2 && ballDirection < 6;
                }
                case 2: {
                    return ballDirection > 4;
                }
                case 4: {
                    return ballDirection > 6 || ballDirection < 2;
                }
                case 6: {
                    return ballDirection > 0 && ballDirection < 4;
                }
            }
        }
        return topItem.getBaseItem().allowStack();
    }

    @Override
    public void onDrag(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction) {
    }

    @Override
    public void onKick(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction) {
    }

    @Override
    public void onTackle(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction) {
    }

    @Override
    public void onMove(Room room, RoomTile from, RoomTile to, RoomUserRotation direction, RoomUnit kicker, int nextRoll, int currentStep, int totalSteps) {
        FootballGame game = (FootballGame)room.getGame(FootballGame.class);
        if (game == null) {
            try {
                game = (FootballGame)FootballGame.class.getDeclaredConstructor(Room.class).newInstance(room);
                room.addGame(game);
            }
            catch (Exception e) {
                return;
            }
        }
        HabboItem currentTopItem = room.getTopItemAt(from.x, from.y, this);
        HabboItem topItem = room.getTopItemAt(to.x, to.y, this);
        if (topItem != null && (currentTopItem == null || currentTopItem.getId() != topItem.getId()) && topItem instanceof InteractionFootballGoal) {
            GameTeamColors color = ((InteractionGameTeamItem)topItem).teamColor;
            game.onScore(kicker, color);
        }
        this.setExtradata("" + Math.abs(currentStep - totalSteps + 1));
        room.sendComposer(new ItemStateComposer(this).compose());
    }

    @Override
    public void onBounce(Room room, RoomUserRotation oldDirection, RoomUserRotation newDirection, RoomUnit kicker) {
    }

    @Override
    public void onStop(Room room, RoomUnit kicker, int currentStep, int totalSteps) {
        this.setExtradata("0");
        room.sendComposer(new ItemStateComposer(this).compose());
    }

    @Override
    public boolean canStillMove(Room room, RoomTile from, RoomTile to, RoomUserRotation direction, RoomUnit kicker, int nextRoll, int currentStep, int totalSteps) {
        HabboItem topItem = room.getTopItemAt(from.x, from.y, this);
        return !(Emulator.getRandom().nextInt(10) >= 3 && room.hasHabbosAt(to.x, to.y) || topItem != null && topItem.getBaseItem().getName().startsWith("fball_goal_") && currentStep != 1);
    }

    @Override
    public void onPickUp(Room room) {
        this.setExtradata("0");
    }
}


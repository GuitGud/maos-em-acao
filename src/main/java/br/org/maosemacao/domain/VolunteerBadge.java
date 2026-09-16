package br.org.maosemacao.domain;
public interface VolunteerBadge {
    String label();
    record Basic() implements VolunteerBadge { public String label() { return "Voluntário"; } }
    record Participation(VolunteerBadge wrapped) implements VolunteerBadge {
        public String label() { return wrapped.label() + " • Mãos presentes"; }
    }
    record Dedication(VolunteerBadge wrapped) implements VolunteerBadge {
        public String label() { return wrapped.label() + " • Dedicação (20h+)"; }
    }
    static String forHours(double hours) {
        VolunteerBadge badge=new Basic();
        if(hours > 0) badge=new Participation(badge);
        if(hours >= 20) badge=new Dedication(badge);
        return badge.label();
    }
}
